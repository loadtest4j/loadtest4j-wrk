package org.loadtest4j.drivers.wrk;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.driver.DriverResult;
import org.loadtest4j.drivers.wrk.junit.UnitTest;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.loadtest4j.drivers.wrk.junit.DriverResultAssert.assertThat;

@Category(UnitTest.class)
public class WrkParserTest {

    private static Reader report(String name) {
        return new InputStreamReader(WrkParserTest.class.getClassLoader().getResourceAsStream("fixtures/reports/" + name), StandardCharsets.UTF_8);
    }

    private static DriverResult driverResult(String name) {
        try (Reader r = report(name)) {
            return Wrk.toDriverResult(r);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testValidReport() {
        assertThat(driverResult("report.json"))
                .hasKo(0)
                .hasOk(1143)
                .hasResponseTimePercentile(73, Duration.ofMillis(1));
    }

    @Test
    public void testReportWithStatusErrors() {
        assertThat(driverResult("status_errors.json"))
                .hasKo(5)
                .hasOk(3);
    }

    @Test
    public void testReportWithSocketErrors() {
        assertThat(driverResult("socket_errors.json"))
                .hasKo(4)
                .hasOk(8);
    }

    @Test
    public void testReportWithSocketAndStatusErrors() {
        assertThat(driverResult("socket_and_status_errors.json"))
                .hasKo(5)
                .hasOk(7);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidReport() {
        driverResult("invalid.json");
    }
}
