package com.github.loadtest4j.drivers.wrk;

import java.time.Duration;

class WrkDuration {
    private WrkDuration() {}

    static Duration parse(String text) {
        if (text.startsWith("PT")) {
            return Duration.parse(text);
        } else {
            final String formattedText = String.format("PT%s", text);
            return Duration.parse(formattedText);
        }
    }
}
