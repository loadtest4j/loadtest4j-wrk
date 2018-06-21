init = function(args)
    local requestsFile = args[1]
    local requestsStr = readFile(requestsFile)
    local parsedRequests = parseJson(requestsStr)

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
    local json = string.format([[
{
    "summary": {
        "bytes": "%d",
        "duration": "%d",
        "errors": {
            "connect": "%d",
            "read": "%d",
            "status": "%d",
            "timeout": "%d",
            "write": "%d"
        },
        "requests": "%d"
    },
    "latency": {
        "mean": "%d",
        "percentiles": {
            "0": "%d",
            "50": "%d",
            "75": "%d",
            "90": "%d",
            "95": "%d",
            "99": "%d",
            "99.9": "%d",
            "100": "%d"
        },
        "stdev": "%d"
    }
}
    ]],
    summary.bytes,
    summary.duration,
    summary.errors.connect,
    summary.errors.read,
    summary.errors.status,
    summary.errors.timeout,
    summary.errors.write,
    summary.requests,
    latency.mean,
    latency.min,
    latency:percentile(50),
    latency:percentile(75),
    latency:percentile(90),
    latency:percentile(95),
    latency:percentile(99),
    latency:percentile(99.9),
    latency.max,
    latency.stdev)

    io.stderr:write(json)
end

function readFile(file)
    local f = io.open(file, "r")
    local content = f:read("*all")
    f:close()
    return content
end

function parseJson(json)
    -- From http://lua.2524044.n2.nabble.com/Smallest-Lua-only-single-file-JSON-parser-tp6934333p6939904.html
    local str = {}
    local escapes = { r='\r', n='\n', b='\b', f='\f', t='\t', Q='"',
        ['\\'] = '\\', ['/']='/' }
    json = json:gsub('([^\\])\\"', '%1\\Q'):gsub('"(.-)"', function(s)
        str[#str+1] = s:gsub("\\(.)", function(c) return escapes[c] end)
        return "$"..#str
    end):gsub("%s", ""):gsub("%[","{"):gsub("%]","}"):gsub("null", "nil")
    json = json:gsub("(%$%d+):", "[%1]="):gsub("%$(%d+)", function(s)
        return ("%q"):format(str[tonumber(s)])
    end)
    return assert(loadstring("return "..json))()
end
