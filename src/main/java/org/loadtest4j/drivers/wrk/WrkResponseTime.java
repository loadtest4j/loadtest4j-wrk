package org.loadtest4j.drivers.wrk;

import org.loadtest4j.driver.DriverResponseTime;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.TreeMap;

class WrkResponseTime implements DriverResponseTime {

    private static final int DECIMAL_PLACES = 3;

    private final TreeMap<BigDecimal, Long> percentiles;

    WrkResponseTime(TreeMap<BigDecimal, Long> percentiles) {
        this.percentiles = percentiles;
    }

    @Override
    public Duration getPercentile(double i) {
        if (i < 0 || i > 100) {
            throw new IllegalArgumentException("A percentile must be between 0 and 100");
        }

        final BigDecimal decimalI = BigDecimal.valueOf(i);

        if (decimalI.scale() > 5) {
            throw new IllegalArgumentException(String.format("The Wrk driver only supports percentile queries up to %d decimal places.", DECIMAL_PLACES));
        }

        final long durationMicroseconds;
        try {
            durationMicroseconds = percentiles.get(decimalI);
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("The Wrk driver could not find a response time value for that percentile.");
        }

        return Duration.ofNanos(durationMicroseconds * 1000);
    }
}
