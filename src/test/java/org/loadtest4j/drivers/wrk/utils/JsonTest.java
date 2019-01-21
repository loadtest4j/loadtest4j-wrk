package org.loadtest4j.drivers.wrk.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.loadtest4j.drivers.wrk.junit.IntegrationTest;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@Category(IntegrationTest.class)
public class JsonTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static Reader json(String name) {
        return new InputStreamReader(JsonTest.class.getClassLoader().getResourceAsStream("fixtures/json/" + name), StandardCharsets.UTF_8);
    }

    public static class Foo {
        @JsonProperty
        String a;
    }

    @Test
    public void shouldRoundTripSuccessfully() throws IOException {
        final Foo input = new Foo();
        input.a = "b";

        final File file = temporaryFolder.newFile("foo.json");

        Json.serialize(file, input);

        final Foo output = Json.parse(new FileReader(file), Foo.class);

        assertThat(output.a).isEqualTo(input.a);
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionOnParseError() throws IOException {
        try (Reader reader = json("invalid.json")) {
            Json.parse(reader, Foo.class);
        }
    }

    @Test(expected = IOException.class)
    public void shouldThrowExceptionOnSerializeError() throws IOException {
        final Foo foo = new Foo();

        final File file = temporaryFolder.newFile("foo.json");

        file.setWritable(false);

        Json.serialize(file, foo);
    }
}
