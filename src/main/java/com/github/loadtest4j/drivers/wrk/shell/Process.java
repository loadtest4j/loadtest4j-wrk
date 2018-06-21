package com.github.loadtest4j.drivers.wrk.shell;

import com.github.loadtest4j.loadtest4j.LoadTesterException;

import java.io.InputStream;

class Process {

    private final java.lang.Process process;

    Process(java.lang.Process process) {
        this.process = process;
    }

    protected InputStream getStderr() {
        return process.getErrorStream();
    }

    protected Integer run() {
        try {
            return process.waitFor();
        } catch (InterruptedException e) {
            throw new LoadTesterException(e);
        }
    }
}
