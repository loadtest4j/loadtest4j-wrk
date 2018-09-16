package org.loadtest4j.drivers.wrk.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.loadtest4j.LoadTesterException;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class Json {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void serialize(File resultFile, Object value) {
        try {
            MAPPER.writeValue(resultFile, value);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    public static <T> T parse(Reader src, Class<T> valueType) {
        try {
            return MAPPER.readValue(src, valueType);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }
}
