init = function(args)
    local requestsFile = args[1]
    local requestsStr = readFile(requestsFile)
    local parsedRequests = decodeJson(requestsStr)

    local r = {}
    for _, req in pairs(parsedRequests.requests) do
        local body = req.body
        local headers = req.headers
        local method = req.method
        local path = req.path
        r[#r + 1] = wrk.format(method, path, headers, body)
    end

    req = table.concat(r)
end

request = function()
  return req
end

-- Writes wrk's output in a machine-readable JSON format.
-- The JSON object mimics the structure of the three argument objects as closely as possible.
done = function(summary, latency, requests)
    local json = {
        ["summary"] = {
            ["duration"] = summary.duration,
            ["errors"] = {
                ["connect"] = summary.errors.connect,
                ["read"] = summary.errors.read,
                ["status"] = summary.errors.status,
                ["timeout"] = summary.errors.timeout,
                ["write"] = summary.errors.write
            },
            ["requests"] = summary.requests
        },
        ["latency"] = {
            ["percentiles"] = {},
            ["stdev"] = latency.stdev
        }
    }

    -- WARNING: decimalPlaces has a connascence with granularity. If you change one you MUST change the other.
    local granularity = 0.001
    local decimalPlaces = 3

    for p = 0, 100, granularity
    do
        -- We must round to the desired decimal places to stop p losing accuracy (e.g. the p95.9 becomes p95.8999999999)
        local roundedP = round(p, decimalPlaces)
        json["latency"]["percentiles"][roundedP] = latency:percentile(roundedP)
    end

    -- p100 is not available so use max instead
    json["latency"]["percentiles"][100] = latency.max

    printReport(encodeJson(json))
end

function printReport(report)
    local outputFile = os.getenv("WRK_OUTPUT")
    if outputFile == nil then
        io.stderr:write(report)
    else
        local fho, _ = io.open(outputFile, "w")
        fho:write(report)
        fho:close()
    end
end

-- From https://stackoverflow.com/a/37792884/1475135
function round(x, n)
    n = math.pow(10, n)
    x = x * n
    if x >= 0 then x = math.floor(x + 0.5) else x = math.ceil(x - 0.5) end
    return x / n
end

function readFile(file)
    local f = io.open(file, "r")
    local content = f:read("*all")
    f:close()
    return content
end

-----------------------------------------------------------------------------
-- JSON helper functions
-- Adapted from: https://github.com/craigmj/json4lua (MIT license)
-----------------------------------------------------------------------------

local math = require('math')
local string = require("string")
local table = require("table")

local json = {}             -- Public namespace
local json_private = {}     -- Private namespace

json.EMPTY_ARRAY={}
json.EMPTY_OBJECT={}

-----------------------------------------------------------------------------
-- MAIN JSON FUNCTIONS
-----------------------------------------------------------------------------

--- Encodes an arbitrary Lua object / variable.
-- @param v The Lua object / variable to be JSON encoded.
-- @return String containing the JSON encoding in internal Lua string format (i.e. not unicode)
function encodeJson(v)
    -- Handle nil values
    if v==nil then
        return "null"
    end

    local vtype = type(v)

    -- Handle strings
    if vtype=='string' then
        return '"' .. json_private.encodeString(v) .. '"'	    -- Need to handle encoding in string
    end

    -- Handle booleans
    if vtype=='number' or vtype=='boolean' then
        return tostring(v)
    end

    -- Handle tables
    if vtype=='table' then
        local rval = {}
        -- Consider arrays separately
        local bArray, maxCount = isArray(v)
        if bArray then
            for i = 1,maxCount do
                table.insert(rval, encodeJson(v[i]))
            end
        else	-- An object, not an array
            for i,j in pairs(v) do
                if isEncodable(i) and isEncodable(j) then
                    table.insert(rval, '"' .. json_private.encodeString(i) .. '":' .. encodeJson(j))
                end
            end
        end
        if bArray then
            return '[' .. table.concat(rval,',') ..']'
        else
            return '{' .. table.concat(rval,',') .. '}'
        end
    end

    -- Handle null values
    if vtype=='function' and v==json.null then
        return 'null'
    end

    assert(false,'encode attempt to encode unsupported type ' .. vtype .. ':' .. tostring(v))
end


--- Decodes a JSON string and returns the decoded value as a Lua data structure / value.
-- @param s The string to scan.
-- @param [startPos] Optional starting position where the JSON string is located. Defaults to 1.
-- @param Lua object, number The object that was scanned, as a Lua table / string / number / boolean or nil,
-- and the position of the first character after
-- the scanned JSON object.
function decodeJson(s, startPos)
    startPos = startPos and startPos or 1
    startPos = decode_scanWhitespace(s,startPos)
    assert(startPos<=string.len(s), 'Unterminated JSON encoded object found at position in [' .. s .. ']')
    local curChar = string.sub(s,startPos,startPos)
    -- Object
    if curChar=='{' then
        return decode_scanObject(s,startPos)
    end
    -- Array
    if curChar=='[' then
        return decode_scanArray(s,startPos)
    end
    -- Number
    if string.find("+-0123456789.e", curChar, 1, true) then
        return decode_scanNumber(s,startPos)
    end
    -- String
    if curChar==[["]] or curChar==[[']] then
        return decode_scanString(s,startPos)
    end
    if string.sub(s,startPos,startPos+1)=='/*' then
        return decodeJson(s, decode_scanComment(s,startPos))
    end
    -- Otherwise, it must be a constant
    return decode_scanConstant(s,startPos)
end

-----------------------------------------------------------------------------
-- PRIVATE JSON FUNCTIONS
-----------------------------------------------------------------------------

function json.null()
    return json.null -- so json.null() will also return null ;-)
end

function decode_scanArray(s,startPos)
    local array = {}	-- The return value
    local stringLen = string.len(s)
    assert(string.sub(s,startPos,startPos)=='[','decode_scanArray called but array does not start at position ' .. startPos .. ' in string:\n'..s )
    startPos = startPos + 1
    -- Infinite loop for array elements
    local index = 1
    repeat
        startPos = decode_scanWhitespace(s,startPos)
        assert(startPos<=stringLen,'JSON String ended unexpectedly scanning array.')
        local curChar = string.sub(s,startPos,startPos)
        if (curChar==']') then
            return array, startPos+1
        end
        if (curChar==',') then
            startPos = decode_scanWhitespace(s,startPos+1)
        end
        assert(startPos<=stringLen, 'JSON String ended unexpectedly scanning array.')
        object, startPos = decodeJson(s,startPos)
        array[index] = object
        index = index + 1
    until false
end

function decode_scanComment(s, startPos)
    assert( string.sub(s,startPos,startPos+1)=='/*', "decode_scanComment called but comment does not start at position " .. startPos)
    local endPos = string.find(s,'*/',startPos+2)
    assert(endPos~=nil, "Unterminated comment in string at " .. startPos)
    return endPos+2
end

function decode_scanConstant(s, startPos)
    local consts = { ["true"] = true, ["false"] = false, ["null"] = nil }
    local constNames = {"true","false","null"}

    for i,k in pairs(constNames) do
        if string.sub(s,startPos, startPos + string.len(k) -1 )==k then
            return consts[k], startPos + string.len(k)
        end
    end
    assert(nil, 'Failed to scan constant from string ' .. s .. ' at starting position ' .. startPos)
end

function decode_scanNumber(s,startPos)
    local endPos = startPos+1
    local stringLen = string.len(s)
    local acceptableChars = "+-0123456789.e"
    while (string.find(acceptableChars, string.sub(s,endPos,endPos), 1, true)
            and endPos<=stringLen
    ) do
        endPos = endPos + 1
    end
    local stringValue = 'return ' .. string.sub(s,startPos, endPos-1)
    local stringEval = load(stringValue)
    assert(stringEval, 'Failed to scan number [ ' .. stringValue .. '] in JSON string at position ' .. startPos .. ' : ' .. endPos)
    return stringEval(), endPos
end

function decode_scanObject(s,startPos)
    local object = {}
    local stringLen = string.len(s)
    local key, value
    assert(string.sub(s,startPos,startPos)=='{','decode_scanObject called but object does not start at position ' .. startPos .. ' in string:\n' .. s)
    startPos = startPos + 1
    repeat
        startPos = decode_scanWhitespace(s,startPos)
        assert(startPos<=stringLen, 'JSON string ended unexpectedly while scanning object.')
        local curChar = string.sub(s,startPos,startPos)
        if (curChar=='}') then
            return object,startPos+1
        end
        if (curChar==',') then
            startPos = decode_scanWhitespace(s,startPos+1)
        end
        assert(startPos<=stringLen, 'JSON string ended unexpectedly scanning object.')
        -- Scan the key
        key, startPos = decodeJson(s,startPos)
        assert(startPos<=stringLen, 'JSON string ended unexpectedly searching for value of key ' .. key)
        startPos = decode_scanWhitespace(s,startPos)
        assert(startPos<=stringLen, 'JSON string ended unexpectedly searching for value of key ' .. key)
        assert(string.sub(s,startPos,startPos)==':','JSON object key-value assignment mal-formed at ' .. startPos)
        startPos = decode_scanWhitespace(s,startPos+1)
        assert(startPos<=stringLen, 'JSON string ended unexpectedly searching for value of key ' .. key)
        value, startPos = decodeJson(s,startPos)
        object[key]=value
    until false	-- infinite loop while key-value pairs are found
end

local escapeSequences = {
    ["\\t"] = "\t",
    ["\\f"] = "\f",
    ["\\r"] = "\r",
    ["\\n"] = "\n",
    ["\\b"] = "\b"
}
setmetatable(escapeSequences, {__index = function(t,k)
    -- skip "\" aka strip escape
    return string.sub(k,2)
end})

function decode_scanString(s,startPos)
    assert(startPos, 'decode_scanString(..) called without start position')
    local startChar = string.sub(s,startPos,startPos)
    -- START SoniEx2
    -- PS: I don't think single quotes are valid JSON
    assert(startChar == [["]] or startChar == [[']],'decode_scanString called for a non-string')
    --assert(startPos, "String decoding failed: missing closing " .. startChar .. " for string at position " .. oldStart)
    local t = {}
    local i,j = startPos,startPos
    while string.find(s, startChar, j+1) ~= j+1 do
        local oldj = j
        i,j = string.find(s, "\\.", j+1)
        local x,y = string.find(s, startChar, oldj+1)
        if not i or x < i then
            i,j = x,y-1
        end
        table.insert(t, string.sub(s, oldj+1, i-1))
        if string.sub(s, i, j) == "\\u" then
            local a = string.sub(s,j+1,j+4)
            j = j + 4
            local n = tonumber(a, 16)
            assert(n, "String decoding failed: bad Unicode escape " .. a .. " at position " .. i .. " : " .. j)
            -- math.floor(x/2^y) == lazy right shift
            -- a % 2^b == bitwise_and(a, (2^b)-1)
            -- 64 = 2^6
            -- 4096 = 2^12 (or 2^6 * 2^6)
            local x
            if n < 0x80 then
                x = string.char(n % 0x80)
            elseif n < 0x800 then
                -- [110x xxxx] [10xx xxxx]
                x = string.char(0xC0 + (math.floor(n/64) % 0x20), 0x80 + (n % 0x40))
            else
                -- [1110 xxxx] [10xx xxxx] [10xx xxxx]
                x = string.char(0xE0 + (math.floor(n/4096) % 0x10), 0x80 + (math.floor(n/64) % 0x40), 0x80 + (n % 0x40))
            end
            table.insert(t, x)
        else
            table.insert(t, escapeSequences[string.sub(s, i, j)])
        end
    end
    table.insert(t,string.sub(j, j+1))
    assert(string.find(s, startChar, j+1), "String decoding failed: missing closing " .. startChar .. " at position " .. j .. "(for string at position " .. startPos .. ")")
    return table.concat(t,""), j+2
    -- END SoniEx2
end

function decode_scanWhitespace(s,startPos)
    local whitespace=" \n\r\t"
    local stringLen = string.len(s)
    while ( string.find(whitespace, string.sub(s,startPos,startPos), 1, true)  and startPos <= stringLen) do
        startPos = startPos + 1
    end
    return startPos
end

local escapeList = {
    ['"']  = '\\"',
    ['\\'] = '\\\\',
    ['/']  = '\\/',
    ['\b'] = '\\b',
    ['\f'] = '\\f',
    ['\n'] = '\\n',
    ['\r'] = '\\r',
    ['\t'] = '\\t'
}

function json_private.encodeString(s)
    local s = tostring(s)
    return s:gsub(".", function(c) return escapeList[c] end) -- SoniEx2: 5.0 compat
end

function isArray(t)
    -- Next we count all the elements, ensuring that any non-indexed elements are not-encodable
    -- (with the possible exception of 'n')
    if (t == json.EMPTY_ARRAY) then return true, 0 end
    if (t == json.EMPTY_OBJECT) then return false end

    local maxIndex = 0
    for k,v in pairs(t) do
        if (type(k)=='number' and math.floor(k)==k and 1<=k) then	-- k,v is an indexed pair
            if (not isEncodable(v)) then return false end	-- All array elements must be encodable
            maxIndex = math.max(maxIndex,k)
        else
            if (k=='n') then
                if v ~= (t.n or #t) then return false end  -- False if n does not hold the number of elements
            else -- Else of (k=='n')
                if isEncodable(v) then return false end
            end  -- End of (k~='n')
        end -- End of k,v not an indexed pair
    end  -- End of loop across all pairs
    return true, maxIndex
end

function isEncodable(o)
    local t = type(o)
    return (t=='string' or t=='boolean' or t=='number' or t=='nil' or t=='table') or
            (t=='function' and o==json.null)
end