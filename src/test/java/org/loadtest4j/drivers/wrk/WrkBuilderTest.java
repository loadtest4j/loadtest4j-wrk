package org.loadtest4j.drivers.wrk;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.driver.Driver;
import org.loadtest4j.drivers.wrk.junit.UnitTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class WrkBuilderTest {

    @Test
    public void shouldHaveDefaultValues() {
        final Wrk wrk = (Wrk) WrkBuilder.withUrl("https://example.com").build();

        assertThat(wrk.connections).isEqualTo(1);
        assertThat(wrk.duration).isEqualTo(Duration.ofSeconds(1));
        assertThat(wrk.executable).isEqualTo("wrk");
        assertThat(wrk.threads).isEqualTo(1);
        assertThat(wrk.url).isEqualTo("https://example.com");
    }

    @Test
    public void shouldSetCustomValues() {
        final Wrk wrk = (Wrk) WrkBuilder.withUrl("https://example.com")
                .withConnections(2)
                .withDuration(Duration.ofSeconds(2))
                .withExecutable("/tmp/wrk")
                .withThreads(2)
                .build();

        assertThat(wrk.connections).isEqualTo(2);
        assertThat(wrk.duration).isEqualTo(Duration.ofSeconds(2));
        assertThat(wrk.executable).isEqualTo("/tmp/wrk");
        assertThat(wrk.threads).isEqualTo(2);
    }

    @Test
    public void shouldBeImmutable() {
        final WrkBuilder builder = WrkBuilder.withUrl("https://example.com");

        final Driver before = builder.build();

        builder.withConnections(2);
        builder.withDuration(Duration.ofSeconds(2));
        builder.withExecutable("/tmp/wrk");
        builder.withThreads(2);

        final Driver after = builder.build();

        assertThat(after).isEqualToComparingFieldByField(before);
    }
}
