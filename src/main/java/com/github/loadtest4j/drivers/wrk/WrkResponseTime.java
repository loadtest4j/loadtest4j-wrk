package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.loadtest4j.ResponseTime;

import java.time.Duration;
import java.util.Map;

class WrkResponseTime implements ResponseTime {

    private final Map<Integer, Long> percentiles;

    WrkResponseTime(Map<Integer, Long> percentiles) {
        this.percentiles = percentiles;
    }

    @Override
    public Duration getPercentile(int i) {
        if (i < 0 || i > 100) {
            throw new IllegalArgumentException("A percentile must be between 0 and 100");
        }

        final long durationMicroseconds = percentiles.get(i);

        return Duration.ofNanos(durationMicroseconds * 1000);
    }
}