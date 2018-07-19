package org.loadtest4j.drivers.wrk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Input {
    @JsonProperty
    private final List<Req> requests;

    public Input(List<Req> requests) {
        this.requests = requests;
    }

    public List<Req> getRequests() {
        return requests;
    }
}
