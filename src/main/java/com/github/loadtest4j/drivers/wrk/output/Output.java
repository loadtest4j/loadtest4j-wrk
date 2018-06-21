package com.github.loadtest4j.drivers.wrk.output;

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
