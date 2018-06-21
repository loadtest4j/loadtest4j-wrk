package com.github.loadtest4j.drivers.wrk.report;

import com.github.loadtest4j.drivers.wrk.shell.output.Output;

import java.net.URI;

public interface WrkReport {
    URI save(Output output);
}
