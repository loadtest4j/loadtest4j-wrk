package org.loadtest4j.drivers.wrk.junit;

import com.xebialabs.restito.semantics.Condition;

import java.util.Collections;
import java.util.List;

public final class MultiPartConditions {

    private static final String HTTP_NEW_LINE = "\r\n";

    private MultiPartConditions() {

    }

    public static Condition withMultipartFormHeader() {
        return Condition.custom(call -> {
            final List<String> contentTypeHeaders = call.getHeaders().getOrDefault("content-type", Collections.emptyList());
            final long matchingHeaders = contentTypeHeaders.stream().filter(header -> header.contains("multipart/form-data")).count();
            return matchingHeaders > 0;
        });
    }

    public static Condition withPostBodyContainingFilePart(String name, String contentType, String content) {
        return Condition.custom(call -> {
            final String body = call.getPostBody();
            return body.contains("Content-Disposition: form-data; filename=\"" + name + "\"" +
                    HTTP_NEW_LINE +
                    HTTP_NEW_LINE +
                    content);
        });
    }

    public static Condition withPostBodyContainingStringPart(String name, String content) {
        return Condition.custom(call -> {
            final String body = call.getPostBody();
            return body.contains("Content-Disposition: form-data; name=\"" + name + "\"" +
                    HTTP_NEW_LINE +
                    HTTP_NEW_LINE +
                    content);
        });
    }
}