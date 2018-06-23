package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.drivers.wrk.junit.UnitTest;
import com.github.loadtest4j.loadtest4j.ResponseTime;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.Duration;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class WrkResponseTimeTest {

    @Test
    public void testGetPercentile() {
        final ResponseTime responseTime = new WrkResponseTime(Collections.singletonMap(50, 1000L));

        assertEquals(Duration.ofMillis(1), responseTime.getPercentile(50));
    }

    @Test
    public void testGetMaxPercentile() {
        final ResponseTime responseTime = new WrkResponseTime(Collections.singletonMap(100, 1000L));

        assertEquals(Duration.ofMillis(1), responseTime.getPercentile(100));
    }

    @Test
    public void testGetMinPercentile() {
        final ResponseTime responseTime = new WrkResponseTime(Collections.singletonMap(0, 1000L));

        assertEquals(Duration.ofMillis(1), responseTime.getPercentile(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTooLowPercentile() {
        final ResponseTime responseTime = new WrkResponseTime(Collections.singletonMap(-1, 1000L));

        assertEquals(Duration.ofMillis(1), responseTime.getPercentile(-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTooHighPercentile() {
        final ResponseTime responseTime = new WrkResponseTime(Collections.singletonMap(101, 1000L));

        assertEquals(Duration.ofMillis(1), responseTime.getPercentile(101));
    }

    @Test(expected = NullPointerException.class)
    public void testGetMissingPercentile() {
        final ResponseTime responseTime = new WrkResponseTime(Collections.emptyMap());

        responseTime.getPercentile(50);
    }
}