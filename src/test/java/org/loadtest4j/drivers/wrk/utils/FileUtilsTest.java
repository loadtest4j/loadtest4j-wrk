package org.loadtest4j.drivers.wrk.utils;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.drivers.wrk.junit.IntegrationTest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@Category(IntegrationTest.class)
public class FileUtilsTest {

    private static InputStream file(String name) {
        return FileUtilsTest.class.getClassLoader().getResourceAsStream("fixtures/files/" + name);
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
    public void shouldCreateFile() {
        final Path path = FileUtils.createTempFile("foo", ".json");

        assertThat(path.toFile()).exists();
    }

    @Test
    public void shouldCopyFile() {
        final Path path = FileUtils.createTempFile("foo", ".json");

        FileUtils.copy(file("foo.json"), path);

        assertThat(path.toFile()).hasContent("{}");
    }

    @Test(expected = LoadTesterException.class)
    public void shouldThrowExceptionOnCopyError() {
        final Path path = FileUtils.createTempFile("foo", ".json");

        final InputStream closed = closed();

        FileUtils.copy(closed, path);
    }
}
