package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.loadtest4j.DriverRequest;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

class WrkLuaScript {
    private final List<DriverRequest> requests;

    WrkLuaScript(List<DriverRequest> requests) {
        this.requests = requests;
    }

    @Override
    public String toString() {
        final StringJoiner s = new StringJoiner("\n");

        s.add("init = function(args)");
        s.add("  local r = {}");
        long i = 1;
        for (DriverRequest request: requests) {
            s.add(String.format("  r[%d] = %s", i, wrkRequest(request)));
            i++;
        }
        s.add("  req = table.concat(r)");
        s.add("end");
        s.add("");
        s.add("request = function()");
        s.add("  return req");
        s.add("end");

        return s.toString();
    }

    private static String wrkRequest(DriverRequest request) {
        final String body = getBody(request);
        final Map<String, String> headers = getHeaders(request);
        final String method = getMethod(request);
        final String path = getPath(request);

        return String.format("wrk.format(%s, %s, %s, %s)", method, path, headers, body);
    }

    private static String getMethod(DriverRequest request) {
        return new LuaMultiLineString(request.getMethod()).toString();
    }

    private static String getBody(DriverRequest request) {
        return new LuaMultiLineString(request.getBody()).toString();
    }

    private static String getPath(DriverRequest request) {
        final String fullPath = request.getPath() + getQueryString(request.getQueryParams());

        return new LuaMultiLineString(fullPath).toString();
    }

    private static String getQueryString(Map<String, String> queryParams) {
        return new QueryString(queryParams).toString();
    }

    private static Map<String, String> getHeaders(DriverRequest request) {
        return new LuaMap(request.getHeaders());
    }
}
