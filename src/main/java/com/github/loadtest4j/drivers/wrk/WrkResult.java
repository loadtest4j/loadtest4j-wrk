package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.loadtest4j.DriverResult;

import java.util.Optional;

class WrkResult implements DriverResult {

    private final long ok;
    private final long ko;
    private final String reportUrl;

    WrkResult(long ok, long ko, String reportUrl) {
        this.ok = ok;
        this.ko = ko;
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
    public Optional<String> getReportUrl() {
        return Optional.of(reportUrl);
    }
}
