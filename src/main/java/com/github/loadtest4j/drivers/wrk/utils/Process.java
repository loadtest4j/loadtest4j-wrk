package com.github.loadtest4j.drivers.wrk.utils;

import com.github.loadtest4j.loadtest4j.LoadTesterException;

import java.io.InputStream;

public class Process {

    private final java.lang.Process process;

    Process(java.lang.Process process) {
        this.process = process;
    }

    public InputStream getStderr() {
        return process.getErrorStream();
    }

    public Integer waitFor() {
        try {
            return process.waitFor();
        } catch (InterruptedException e) {
            throw new LoadTesterException(e);
        }
    }
}
