package com.github.loadtest4j.drivers.wrk.shell.input;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Req {
    @JsonProperty
    private final String body;

    @JsonProperty
    private final Map<String, String> headers;

    @JsonProperty
    private final String method;

    @JsonProperty
    private final String path;

    public Req(String body, Map<String, String> headers, String method, String path) {
        this.body = body;
        this.headers = headers;
        this.method = method;
        this.path = path;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
