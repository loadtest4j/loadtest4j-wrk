package org.loadtest4j.drivers.wrk;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.driver.DriverResult;
import org.loadtest4j.drivers.wrk.junit.UnitTest;

import java.net.URL;
import java.time.Duration;

import static org.loadtest4j.drivers.wrk.junit.DriverResultAssert.assertThat;

@Category(UnitTest.class)
public class WrkParserTest {

    private static URL report(String name) {
        return WrkParserTest.class.getClassLoader().getResource("fixtures/reports/" + name);
    }

    @Test
    public void testValidReport() {
        final URL report = report("report.json");

        final DriverResult driverResult = Wrk.toDriverResult(report);

        assertThat(driverResult)
                .hasKo(0)
                .hasOk(1143)
                .hasResponseTimePercentile(73, Duration.ofMillis(1))
                .hasReportUrlWithScheme("file");
    }

    @Test
    public void testReportWithStatusErrors() {
        final URL report = report("status_errors.json");

        final DriverResult driverResult = Wrk.toDriverResult(report);

        assertThat(driverResult)
                .hasKo(5)
                .hasOk(3);
    }

    @Test
    public void testReportWithSocketErrors() {
        final URL report = report("socket_errors.json");

        final DriverResult driverResult = Wrk.toDriverResult(report);

        assertThat(driverResult)
                .hasKo(4)
                .hasOk(8);
    }

    @Test
    public void testReportWithSocketAndStatusErrors() {
        final URL report = report("socket_and_status_errors.json");

        final DriverResult driverResult = Wrk.toDriverResult(report);

        assertThat(driverResult)
                .hasKo(5)
                .hasOk(7);
    }

    @Test(expected = LoadTesterException.class)
    public void testInvalidReport() {
        final URL report = report("invalid.json");

        Wrk.toDriverResult(report);
    }
}
