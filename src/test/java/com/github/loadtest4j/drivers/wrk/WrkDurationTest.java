package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.drivers.wrk.junit.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.Duration;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class WrkDurationTest {
    @Test
    public void testParseWithoutPrefix() {
        final Duration duration = WrkDuration.parse("30.11s");

        assertEquals(Duration.ofMillis(30110), duration);
    }

    @Test
    public void testParseWithPrefix() {
        final Duration duration = WrkDuration.parse("PT30.11s");

        assertEquals(Duration.ofMillis(30110), duration);
    }
}
