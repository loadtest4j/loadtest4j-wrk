package com.github.loadtest4j.drivers.wrk.utils;

import com.github.loadtest4j.loadtest4j.LoadTesterException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AutoDeletingTempFile implements AutoCloseable {

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

    public String getAbsolutePath() {
        return path.toAbsolutePath().toString();
    }

    public static AutoDeletingTempFile create(InputStream inputStream) {
        try {
            final Path path = Files.createTempFile(null, null);
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            return new AutoDeletingTempFile(path);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    public static AutoDeletingTempFile create(String contents) {
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
