package org.loadtest4j.drivers.wrk.script;

import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WrkHeadersMatcher implements Body.Matcher<Map<String, String>> {

    private final Map<String, String> headers;

    public WrkHeadersMatcher(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Map<String, String> string(String body) {
        return headers;
    }

    @Override
    public Map<String, String> multipart(List<BodyPart> body) {
        return concatMaps(headers, Collections.singletonMap("Content-Type", "multipart/form-data; boundary=" + MultipartBoundary.STANDARD));
    }

    private static Map<String, String> concatMaps(Map<String, String> a, Map<String, String> b) {
        return Stream.concat(a.entrySet().stream(), b.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
