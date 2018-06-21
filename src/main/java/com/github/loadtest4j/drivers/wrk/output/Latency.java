package com.github.loadtest4j.drivers.wrk.output;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

class Latency {
    @JsonProperty
    private Map<Double, Long> percentiles;

    @JsonProperty
    private long stdev;

    public Map<Double, Long> getPercentiles() {
        return percentiles;
    }

    public long getStdev() {
        return stdev;
    }
}
