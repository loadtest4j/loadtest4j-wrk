package org.loadtest4j.drivers.wrk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Output {
    @JsonProperty
    private Summary summary;

    @JsonProperty
    private Latency latency;

    public Summary getSummary() {
        return summary;
    }

    public Latency getLatency() {
        return latency;
    }
}
