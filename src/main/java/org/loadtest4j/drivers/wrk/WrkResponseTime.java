package org.loadtest4j.drivers.wrk;

import org.loadtest4j.driver.DriverResponseTime;

import java.time.Duration;
import java.util.Map;

class WrkResponseTime implements DriverResponseTime {

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
