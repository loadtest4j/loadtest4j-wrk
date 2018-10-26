package org.loadtest4j.drivers.wrk.script;

import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.drivers.wrk.utils.ContentTypes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class WrkBodyMatcher implements Body.Matcher<String> {

    private static final String BODY_BOUNDARY = "--" + MultipartBoundary.STANDARD;
    private static final String CRLF = "\r\n";
    private static final String BOTTOM_BOUNDARY = "--" + MultipartBoundary.STANDARD + "--";

    @Override
    public String string(String body) {
        return body;
    }

    @Override
    public String multipart(List<BodyPart> body) {
        final StringBuilder sb = new StringBuilder();
        for (BodyPart bodyPart: body) {
            final BodyPartMatcherResult result = bodyPart.match(new WrkBodyPartMatcher());
            final String contentDisposition = result.contentDisposition;
            final String content = result.content;

            sb.append(BODY_BOUNDARY)
                    .append(CRLF)
                    .append(contentDisposition)
                    .append(CRLF);

            if (result.contentType != null) {
                sb.append("Content-Type: ")
                        .append(result.contentType)
                        .append(CRLF);
            }

            sb.append(CRLF)
            .append(content)
            .append(CRLF);
        }
        sb.append(BOTTOM_BOUNDARY);

        return sb.toString();
    }

    private static class WrkBodyPartMatcher implements BodyPart.Matcher<BodyPartMatcherResult> {

        @Override
        public BodyPartMatcherResult stringPart(String name, String content) {
            final String contentDisposition = "Content-Disposition: form-data; name=\"" + name + "\"";
            return new BodyPartMatcherResult(content, contentDisposition, null);
        }

        @Override
        public BodyPartMatcherResult filePart(Path file) {
            final String name = Optional.ofNullable(file.getFileName()).orElseThrow(NullPointerException::new).toString();
            final String contentDisposition = "Content-Disposition: form-data; filename=\"" + name + "\"";
            final String content = readFileToString(file);
            final String contentType = ContentTypes.detect(file);
            return new BodyPartMatcherResult(content, contentDisposition, contentType);
        }
    }

    private static class BodyPartMatcherResult {

        private final String content;
        private final String contentDisposition;
        private final String contentType;

        private BodyPartMatcherResult(String content, String contentDisposition, String contentType) {
            this.content = content;
            this.contentDisposition = contentDisposition;
            this.contentType = contentType;
        }
    }

    private static String readFileToString(Path file) {
        try {
            return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }
}
