package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.drivers.wrk.junit.IntegrationTest;
import com.github.loadtest4j.drivers.wrk.dto.Output;
import com.github.loadtest4j.loadtest4j.LoadTesterException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@Category(IntegrationTest.class)
public class WrkReportTest {
    @Test
    public void testSave() {
        final WrkReport wrkReport = new WrkReport();

        final URI uri = wrkReport.save(new Output());

        assertThat(uri).hasScheme("file");
    }

    @Test(expected = LoadTesterException.class)
    public void testSaveWithIOException() {
        final WrkReport wrkReport = new ExceptionalWrkReport();

        wrkReport.save(new Output());
    }

    private static class ExceptionalWrkReport extends WrkReport {
        @Override
        protected File writeJson(Output output) throws IOException {
            throw new IOException();
        }
    }
}
