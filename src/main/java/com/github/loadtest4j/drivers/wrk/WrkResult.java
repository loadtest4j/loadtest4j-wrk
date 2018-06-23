package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.loadtest4j.ResponseTime;
import com.github.loadtest4j.loadtest4j.driver.DriverResult;

import java.time.Duration;
import java.util.Optional;

class WrkResult implements DriverResult {

    private final long ok;
    private final long ko;
    private final Duration actualDuration;
    private final ResponseTime responseTime;
    private final String reportUrl;

    WrkResult(long ok, long ko, Duration actualDuration, ResponseTime responseTime, String reportUrl) {
        this.ok = ok;
        this.ko = ko;
        this.actualDuration = actualDuration;
        this.responseTime = responseTime;
        this.reportUrl = reportUrl;
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
    public ResponseTime getResponseTime() {
        return responseTime;
    }

    @Override
    public Optional<String> getReportUrl() {
        return Optional.of(reportUrl);
    }
}
