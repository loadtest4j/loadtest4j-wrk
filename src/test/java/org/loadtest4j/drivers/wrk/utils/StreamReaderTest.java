package org.loadtest4j.drivers.wrk.utils;

import org.loadtest4j.drivers.wrk.junit.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class StreamReaderTest {

    @Test
    public void shouldRoundTripSuccessfully() {
        final String input = "foo";

        final String output = StreamReader.streamToString(StreamReader.stringToStream(input));

        assertThat(output).isEqualTo(input);
    }
}
