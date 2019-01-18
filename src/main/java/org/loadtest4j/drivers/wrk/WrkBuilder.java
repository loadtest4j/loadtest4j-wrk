package org.loadtest4j.drivers.wrk;

import org.loadtest4j.factory.LoadTesterBuilder;

import java.time.Duration;

public class WrkBuilder extends LoadTesterBuilder {
    private final int connections;
    private final Duration duration;
    private final String executable;
    private final int threads;
    private final String url;

    private WrkBuilder(int connections, Duration duration, String executable, int threads, String url) {
        this.connections = connections;
        this.duration = duration;
        this.executable = executable;
        this.threads = threads;
        this.url = url;
    }

    public static WrkBuilder withUrl(String url) {
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

    @Override
    protected Wrk buildDriver() {
        return new Wrk(connections, duration, executable, threads, url);
    }
}
