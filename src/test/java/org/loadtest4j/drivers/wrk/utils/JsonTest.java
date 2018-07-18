package org.loadtest4j.drivers.wrk.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.loadtest4j.drivers.wrk.junit.IntegrationTest;
import com.github.loadtest4j.loadtest4j.LoadTesterException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@Category(IntegrationTest.class)
public class JsonTest {

    private static URL json(String name) {
        return JsonTest.class.getClassLoader().getResource("fixtures/json/" + name);
    }

    public static class Foo {
        @JsonProperty
        String a;
    }

    @Test
    public void testRoundTrip() {
        final Foo input = new Foo();
        input.a = "b";

        File file = TempFile.createTempFile("foo", ".json").toFile();

        Json.serialize(file, input);

        final Foo output = Json.parse(toUrl(file), Foo.class);

        assertThat(output.a).isEqualTo(input.a);
    }

    @Test(expected = LoadTesterException.class)
    public void testParseError() {
        final URL url = json("invalid.json");

        Json.parse(url, Foo.class);
    }

    @Test(expected = LoadTesterException.class)
    public void testSerializeError() {
        final Foo foo = new Foo();

        File file = TempFile.createTempFile("foo", ".json").toFile();
        file.setWritable(false);

        Json.serialize(file, foo);
    }

    private static URL toUrl(File file) {
        final URL url;
        try {
            url = file.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Should have converted from File to URL");
        }
        return url;
    }
}
