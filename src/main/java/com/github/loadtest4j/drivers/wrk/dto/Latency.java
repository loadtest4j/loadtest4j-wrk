package com.github.loadtest4j.drivers.wrk.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Latency {
    @JsonProperty
    private Map<Integer, Long> percentiles;

    public Map<Integer, Long> getPercentiles() {
        return percentiles;
    }
}
