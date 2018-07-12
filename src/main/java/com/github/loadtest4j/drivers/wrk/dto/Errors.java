package com.github.loadtest4j.drivers.wrk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Errors {
    @JsonProperty
    private long connect;

    @JsonProperty
    private long read;

    @JsonProperty
    private long status;

    @JsonProperty
    private long timeout;

    @JsonProperty
    private long write;

    public long getConnect() {
        return connect;
    }

    public long getRead() {
        return read;
    }

    public long getStatus() {
        return status;
    }

    public long getTimeout() {
        return timeout;
    }

    public long getWrite() {
        return write;
    }
}
