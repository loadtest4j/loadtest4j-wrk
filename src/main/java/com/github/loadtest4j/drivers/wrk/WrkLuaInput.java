package com.github.loadtest4j.drivers.wrk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.loadtest4j.drivers.wrk.input.Input;
import com.github.loadtest4j.drivers.wrk.input.Req;
import com.github.loadtest4j.loadtest4j.DriverRequest;
import com.github.loadtest4j.loadtest4j.LoadTesterException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class WrkLuaInput {

    protected static AutoDeletingTempFile create(List<DriverRequest> requests) {
        final List<Req> reqs = wrkRequests(requests);

        final Input input = new Input(reqs);

        final String serializedRequests = serialize(input);

        return AutoDeletingTempFile.create(serializedRequests);
    }

    private static List<Req> wrkRequests(List<DriverRequest> requests) {
        return requests.stream()
                .map(WrkLuaInput::wrkRequest)
                .collect(Collectors.toList());
    }

    private static Req wrkRequest(DriverRequest request) {
        final String body = request.getBody();
        final Map<String, String> headers = request.getHeaders();
        final String method = request.getMethod();
        final String path = getPath(request);

        return new Req(body, headers, method, path);
    }

    private static String getPath(DriverRequest request) {
        return request.getPath() + getQueryString(request.getQueryParams());
    }

    private static String getQueryString(Map<String, String> queryParams) {
        return new QueryString(queryParams).toString();
    }

    private static String serialize(Input input) {
        try {
            return new ObjectMapper().writeValueAsString(input);
        } catch (JsonProcessingException e) {
            throw new LoadTesterException(e);
        }
    }


}
