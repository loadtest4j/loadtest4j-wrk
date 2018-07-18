package org.loadtest4j.drivers.wrk.utils;

import org.loadtest4j.drivers.wrk.junit.IntegrationTest;
import com.github.loadtest4j.loadtest4j.LoadTesterException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@Category(IntegrationTest.class)
public class TempFileTest {

    private static InputStream file(String name) {
        return TempFileTest.class.getClassLoader().getResourceAsStream("fixtures/files/" + name);
    }

    private static InputStream closed() {
        final InputStream closed = file("foo.json");
        try {
            closed.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return closed;
    }

    @Test
    public void testCreate() {
        final Path path = TempFile.createTempFile("foo", ".json");

        assertThat(path.toFile()).exists();
    }

    @Test
    public void testCopy() {
        final Path path = TempFile.createTempFile("foo", ".json");

        TempFile.copy(file("foo.json"), path);

        assertThat(path.toFile()).hasContent("{}");
    }

    @Test(expected = LoadTesterException.class)
    public void testCopyError() {
        final Path path = TempFile.createTempFile("foo", ".json");

        final InputStream closed = closed();

        TempFile.copy(closed, path);
    }
}
