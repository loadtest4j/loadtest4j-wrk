package com.github.loadtest4j.drivers.wrk.utils;

import com.github.loadtest4j.drivers.wrk.junit.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class StreamReaderTest {

    private static InputStream toStream(String str) {
        return new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testStreamToString() {
        final InputStream stream = toStream("foo");

        final String output = StreamReader.streamToString(stream);

        assertThat(output).isEqualTo("foo");
    }
}
