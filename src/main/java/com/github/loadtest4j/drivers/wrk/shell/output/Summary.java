package com.github.loadtest4j.drivers.wrk.shell.output;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Summary {
    @JsonProperty
    private long bytes;

    @JsonProperty
    private long duration;

    @JsonProperty
    private Errors errors;

    @JsonProperty
    private long requests;

    public long getBytes() {
        return bytes;
    }

    public long getDuration() {
        return duration;
    }

    public Errors getErrors() {
        return errors;
    }

    public long getRequests() {
        return requests;
    }
}