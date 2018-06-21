package com.github.loadtest4j.drivers.wrk.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.loadtest4j.drivers.wrk.shell.output.Output;
import com.github.loadtest4j.loadtest4j.LoadTesterException;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class LocalWrkReport implements WrkReport {
    @Override
    public URI save(Output output) {
        try {
            final File reportFile = File.createTempFile("wrk", "json");

            new ObjectMapper().writeValue(reportFile, output);

            return reportFile.toPath().toUri();
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }
}
