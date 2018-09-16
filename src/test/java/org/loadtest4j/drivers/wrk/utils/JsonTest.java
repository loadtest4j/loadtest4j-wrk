package org.loadtest4j.drivers.wrk.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.drivers.wrk.junit.IntegrationTest;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@Category(IntegrationTest.class)
public class JsonTest {

    private static Reader json(String name) {
        return new InputStreamReader(JsonTest.class.getClassLoader().getResourceAsStream("fixtures/json/" + name), StandardCharsets.UTF_8);
    }

    public static class Foo {
        @JsonProperty
        String a;
    }

    @Test
    public void testRoundTrip() throws Exception {
        final Foo input = new Foo();
        input.a = "b";

        File file = FileUtils.createTempFile("foo", ".json").toFile();

        Json.serialize(file, input);

        final Foo output = Json.parse(new FileReader(file), Foo.class);

        assertThat(output.a).isEqualTo(input.a);
    }

    @Test(expected = LoadTesterException.class)
    public void testParseError() throws Exception {
        try (Reader reader = json("invalid.json")) {
            Json.parse(reader, Foo.class);
        }
    }

    @Test(expected = LoadTesterException.class)
    public void testSerializeError() {
        final Foo foo = new Foo();

        File file = FileUtils.createTempFile("foo", ".json").toFile();
        file.setWritable(false);

        Json.serialize(file, foo);
    }
}
