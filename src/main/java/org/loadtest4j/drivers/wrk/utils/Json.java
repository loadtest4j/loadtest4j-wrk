package org.loadtest4j.drivers.wrk.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class Json {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void serialize(File resultFile, Object value) throws IOException {
        MAPPER.writeValue(resultFile, value);
    }

    public static <T> T parse(Reader src, Class<T> valueType) throws IOException {
        return MAPPER.readValue(src, valueType);
    }
}
