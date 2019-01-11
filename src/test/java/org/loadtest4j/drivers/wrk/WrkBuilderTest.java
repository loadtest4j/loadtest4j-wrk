package org.loadtest4j.drivers.wrk;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.drivers.wrk.junit.UnitTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Category(UnitTest.class)
public class WrkBuilderTest {

    private final WrkBuilder builder = WrkBuilder.standard("https://example.com");

    @Test
    public void shouldHaveDefaultValues() {
        assertThat(builder.connections).isEqualTo(1);
        assertThat(builder.duration).isEqualTo(Duration.ofSeconds(1));
        assertThat(builder.executable).isEqualTo("wrk");
        assertThat(builder.threads).isEqualTo(1);
        assertThat(builder.url).isEqualTo("https://example.com");
    }

    @Test
    public void shouldSetConnections() {
        final WrkBuilder b = builder.withConnections(2);

        assertThat(b.connections).isEqualTo(2);
    }

    @Test
    public void shouldSetDuration() {
        final WrkBuilder b = builder.withDuration(Duration.ofSeconds(2));

        assertThat(b.duration).isEqualTo(Duration.ofSeconds(2));
    }

    @Test
    public void shouldSetExecutable() {
        final WrkBuilder b = builder.withExecutable("/tmp/wrk");

        assertThat(b.executable).isEqualTo("/tmp/wrk");
    }

    @Test
    public void shouldSetThreads() {
        final WrkBuilder b = builder.withThreads(2);

        assertThat(b.threads).isEqualTo(2);
    }

    @Test
    public void shouldBuild() {
        fail("implement me");
    }
}
