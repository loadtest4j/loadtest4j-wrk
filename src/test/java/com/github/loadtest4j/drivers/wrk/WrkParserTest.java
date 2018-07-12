package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.drivers.wrk.junit.UnitTest;
import com.github.loadtest4j.loadtest4j.driver.DriverResult;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.URL;
import java.time.Duration;

import static com.github.loadtest4j.drivers.wrk.junit.DriverResultAssert.assertThat;

@Category(UnitTest.class)
public class WrkParserTest {

    private static URL report(String name) {
        return WrkParserTest.class.getClassLoader().getResource("fixtures/" + name);
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
}
