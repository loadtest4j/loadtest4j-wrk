package org.loadtest4j.drivers.wrk.utils;

import org.loadtest4j.LoadTesterException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtils {
    public static Path createTempFile(String prefix, String suffix) {
        try {
            return Files.createTempFile(prefix, suffix);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    public static void copy(InputStream content, Path target) {
        try {
            Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }
}
