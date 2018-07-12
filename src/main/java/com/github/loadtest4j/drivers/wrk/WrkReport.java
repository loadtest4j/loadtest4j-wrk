package com.github.loadtest4j.drivers.wrk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.loadtest4j.drivers.wrk.dto.Output;
import com.github.loadtest4j.loadtest4j.LoadTesterException;

import java.io.File;
import java.io.IOException;
import java.net.URI;

class WrkReport {
    protected URI save(Output output) {
        try {
            final File reportFile = writeJson(output);

            return reportFile.toPath().toUri();
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    protected File writeJson(Output output) throws IOException {
        final File file = File.createTempFile("wrk", "json");
        new ObjectMapper().writeValue(file, output);
        return file;
    }
}
