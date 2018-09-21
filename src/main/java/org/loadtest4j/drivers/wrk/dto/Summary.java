package org.loadtest4j.drivers.wrk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Summary {
    @JsonProperty
    private long duration;

    @JsonProperty
    private Errors errors;

    @JsonProperty
    private long requests;

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
