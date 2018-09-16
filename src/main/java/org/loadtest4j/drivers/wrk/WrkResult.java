package org.loadtest4j.drivers.wrk;

import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.driver.DriverResult;

import java.time.Duration;
import java.util.Optional;

class WrkResult implements DriverResult {

    private final long ok;
    private final long ko;
    private final Duration actualDuration;
    private final DriverResponseTime responseTime;

    WrkResult(long ok, long ko, Duration actualDuration, DriverResponseTime responseTime) {
        this.ok = ok;
        this.ko = ko;
        this.actualDuration = actualDuration;
        this.responseTime = responseTime;
    }

    @Override
    public long getOk() {
        return ok;
    }

    @Override
    public long getKo() {
        return ko;
    }

    @Override
    public Duration getActualDuration() {
        return actualDuration;
    }

    @Override
    public DriverResponseTime getResponseTime() {
        return responseTime;
    }

    @Override
    public Optional<String> getReportUrl() {
        return Optional.empty();
    }
}
