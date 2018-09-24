package org.loadtest4j.drivers.wrk.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.TreeMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Latency {
    /**
     * A mapping of (the percentile, decimal) to (the percentile value).
     */
    @JsonProperty
    private TreeMap<BigDecimal, Long> percentiles;

    public TreeMap<BigDecimal, Long> getPercentiles() {
        return percentiles;
    }
}
