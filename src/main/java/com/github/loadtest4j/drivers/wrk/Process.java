package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.loadtest4j.LoadTesterException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

class Process {

    private final java.lang.Process process;

    Process(java.lang.Process process) {
        this.process = process;
    }

    protected String readStdout() {
        // From https://stackoverflow.com/a/5445161
        final InputStream istream = process.getInputStream();
        final Scanner scanner = new Scanner(istream, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    protected Integer run() {
        try {
            return process.waitFor();
        } catch (InterruptedException e) {
            throw new LoadTesterException(e);
        }
    }
}
