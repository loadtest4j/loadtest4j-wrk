package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.loadtest4j.LoadTesterException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

class AutoDeletingTempFile implements AutoCloseable {

    private final Path path;

    private AutoDeletingTempFile(Path path) {
        this.path = path;
    }

    @Override
    public void close() {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    protected boolean exists() {
        return Files.exists(path);
    }

    protected String getAbsolutePath() {
        return path.toAbsolutePath().toString();
    }

    protected static AutoDeletingTempFile create(String contents) {
        try {
            final Path path = Files.createTempFile(null, null);
            writeStringToFile(path.toFile(), contents);
            return new AutoDeletingTempFile(path);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    private static void writeStringToFile(File file, String contents) throws IOException {
        try (Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            w.write(contents);
        }
    }
}
