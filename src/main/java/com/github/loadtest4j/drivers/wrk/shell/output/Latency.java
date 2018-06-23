package com.github.loadtest4j.drivers.wrk.shell.output;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Latency {
    @JsonProperty
    private long mean;

    @JsonProperty
    private Map<Integer, Long> percentiles;

    @JsonProperty
    private long stdev;

    public long getMean() {
        return mean;
    }

    public Map<Integer, Long> getPercentiles() {
        return percentiles;
    }

    public long getStdev() {
        return stdev;
    }
}
