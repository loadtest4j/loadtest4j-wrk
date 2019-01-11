package org.loadtest4j.drivers.wrk;

import org.loadtest4j.driver.Driver;

import java.time.Duration;

public class WrkBuilder {
    final int connections;
    final Duration duration;
    final String executable;
    final int threads;
    final String url;

    private WrkBuilder(int connections, Duration duration, String executable, int threads, String url) {
        this.connections = connections;
        this.duration = duration;
        this.executable = executable;
        this.threads = threads;
        this.url = url;
    }

    public static WrkBuilder standard(String url) {
        return new WrkBuilder(1, Duration.ofSeconds(1), "wrk", 1, url);
    }

    public WrkBuilder withConnections(int connections) {
        return new WrkBuilder(connections, duration, executable, threads, url);
    }

    public WrkBuilder withDuration(Duration duration) {
        return new WrkBuilder(connections, duration, executable, threads, url);
    }

    public WrkBuilder withExecutable(String executable) {
        return new WrkBuilder(connections, duration, executable, threads, url);
    }

    public WrkBuilder withThreads(int threads) {
        return new WrkBuilder(connections, duration, executable, threads, url);
    }

    public Driver build() {
        return new Wrk(connections, duration, executable, threads, url);
    }
}
