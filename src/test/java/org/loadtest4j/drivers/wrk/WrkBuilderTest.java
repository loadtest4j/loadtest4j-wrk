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
        final Wrk wrk = (Wrk) builder.build();

        assertThat(wrk.url).isEqualTo("https://example.com");
    }

    @Test
    public void shouldSetConnections() {
        final Wrk wrk = (Wrk) builder
                .withConnections(2)
                .build();

        assertThat(wrk.connections).isEqualTo(2);
    }

    @Test
    public void shouldSetConnectionsTo1ByDefault() {
        final Wrk wrk = (Wrk) builder.build();

        assertThat(wrk.connections).isEqualTo(1);
    }

    @Test
    public void shouldSetDuration() {
        final Wrk wrk = (Wrk) builder
                .withDuration(Duration.ofSeconds(2))
                .build();

        assertThat(wrk.duration).isEqualTo(Duration.ofSeconds(2));
    }

    @Test
    public void shouldSetDurationTo1SecondByDefault() {
        final Wrk wrk = (Wrk) builder.build();

        assertThat(wrk.duration).isEqualTo(Duration.ofSeconds(1));
    }

    @Test
    public void shouldSetExecutable() {
        final Wrk wrk = (Wrk) builder
                .withExecutable("/tmp/wrk")
                .build();

        assertThat(wrk.executable).isEqualTo("/tmp/wrk");
    }

    @Test
    public void shouldSetExecutableToWrkByDefault() {
        final Wrk wrk = (Wrk) builder.build();

        assertThat(wrk.executable).isEqualTo("wrk");
    }

    @Test
    public void shouldSetThreads() {
        final Wrk wrk = (Wrk) builder
                .withThreads(2)
                .build();

        assertThat(wrk.threads).isEqualTo(2);
    }

    @Test
    public void shouldSetThreadsTo1ByDefault() {
        final Wrk wrk = (Wrk) builder.build();

        assertThat(wrk.threads).isEqualTo(1);
    }

    @Test
    public void shouldBeImmutable() {
        final Driver before = builder.build();

        builder.withConnections(2);
        builder.withDuration(Duration.ofSeconds(2));
        builder.withExecutable("/tmp/wrk");
        builder.withThreads(2);

        final Driver after = builder.build();

        assertThat(after).isEqualToComparingFieldByField(before);
    }
}
