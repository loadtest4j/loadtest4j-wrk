package org.loadtest4j.drivers.wrk;

import org.loadtest4j.driver.Driver;
import org.loadtest4j.driver.DriverFactory;

import java.time.Duration;
import java.util.*;

public class WrkFactory implements DriverFactory {

    @Override
    public Set<String> getMandatoryProperties() {
        return setOf("connections", "duration", "threads", "url");
    }

    /**
     * Creates a Wrk driver using the following properties.
     *
     * Mandatory properties:
     *
     * - `connections`
     * - `duration`
     * - `threads`
     * - `url`
     *
     * Optional properties:
     *
     * - `executable` (defaults to `wrk`, and is located using the PATH)
     */
    @Override
    public Driver create(Map<String, String> properties) {
        final Duration duration = Duration.ofSeconds(Long.parseLong(properties.get("duration")));
        final int connections = Integer.parseInt(properties.get("connections"));
        final String executable = properties.getOrDefault("executable", "wrk");
        final int threads = Integer.parseInt(properties.get("threads"));
        final String url = properties.get("url");

        return new Wrk(connections, duration, executable, threads, url);
    }

    private static Set<String> setOf(String... values) {
        // This utility method can be replaced when Java 9+ is more widely adopted
        final Set<String> internalSet = new LinkedHashSet<>(Arrays.asList(values));
        return Collections.unmodifiableSet(internalSet);
    }
}
