package com.github.loadtest4j.drivers.wrk;

import java.time.Duration;
import java.util.Optional;

class WrkDuration {
    private WrkDuration() {}

    protected static Duration parse(String text) {
        // Duration.parse accepts the following ASCII suffixes:
        // "D", "H", "M" and "S" mark days, hours, minutes and seconds, in upper or lower case.
        final Optional<Duration> millis = parseMilliseconds(text);
        if (millis.isPresent()) {
            return millis.get();
        }

        final Optional<Duration> micros = parseMicroseconds(text);
        if (micros.isPresent()) {
            return micros.get();
        }

        return parseIso8601(text);
    }

    private static Optional<Duration> parseMilliseconds(String text) {
        if (text.endsWith("ms")) {
            final Optional<Double> millis = parseDouble(text.replace("ms", ""));
            return millis.map(theMillis -> Duration.ofMillis(doubleToLong(theMillis)));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<Duration> parseMicroseconds(String text) {
        if (text.endsWith("us")) {
            final Optional<Double> micros = parseDouble(text.replace("us", ""));
            return micros.map(theMicros -> Duration.ofNanos(doubleToLong(theMicros * 1000)));
        } else {
            return Optional.empty();
        }
    }

    private static Duration parseIso8601(String text) {
        if (text.startsWith("PT")) {
            return Duration.parse(text);
        } else {
            final String formattedText = String.format("PT%s", text);
            return Duration.parse(formattedText);
        }
    }

    private static Optional<Double> parseDouble(String text) {
        try {
            final double number = Double.parseDouble(text);
            return Optional.of(number);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static long doubleToLong(double d) {
        return Math.round(d);
    }
}
