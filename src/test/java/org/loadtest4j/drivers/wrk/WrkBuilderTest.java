package org.loadtest4j.drivers.wrk;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.driver.Driver;
import org.loadtest4j.drivers.wrk.junit.UnitTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class WrkBuilderTest {

    private final WrkBuilder builder = WrkBuilder.withUrl("https://example.com");

    @Test
    public void shouldRequireUrl() {
        final Wrk wrk = builder.buildDriver();

        assertThat(wrk.url).isEqualTo("https://example.com");
    }

    @Test
    public void shouldSetConnections() {
        final Wrk wrk = builder
                .withConnections(2)
                .buildDriver();

        assertThat(wrk.connections).isEqualTo(2);
    }

    @Test
    public void shouldSetConnectionsTo1ByDefault() {
        final Wrk wrk = builder.buildDriver();

        assertThat(wrk.connections).isEqualTo(1);
    }

    @Test
    public void shouldSetDuration() {
        final Wrk wrk = builder
                .withDuration(Duration.ofSeconds(2))
                .buildDriver();

        assertThat(wrk.duration).isEqualTo(Duration.ofSeconds(2));
    }

    @Test
    public void shouldSetDurationTo1SecondByDefault() {
        final Wrk wrk = builder.buildDriver();

        assertThat(wrk.duration).isEqualTo(Duration.ofSeconds(1));
    }

    @Test
    public void shouldSetExecutableToWrkByDefault() {
        final Wrk wrk = builder.buildDriver();

        assertThat(wrk.executable).isEqualTo("wrk");
    }

    @Test
    public void shouldSetThreads() {
        final Wrk wrk = builder
                .withThreads(2)
                .buildDriver();

        assertThat(wrk.threads).isEqualTo(2);
    }

    @Test
    public void shouldSetThreadsTo1ByDefault() {
        final Wrk wrk = builder.buildDriver();

        assertThat(wrk.threads).isEqualTo(1);
    }

    @Test
    public void shouldBeImmutable() {
        final Driver before = builder.buildDriver();

        builder.withConnections(2);
        builder.withDuration(Duration.ofSeconds(2));
        builder.withThreads(2);

        final Driver after = builder.buildDriver();

        assertThat(after).isEqualToComparingFieldByField(before);
    }
}
