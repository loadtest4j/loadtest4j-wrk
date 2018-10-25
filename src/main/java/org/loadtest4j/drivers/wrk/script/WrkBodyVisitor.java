package org.loadtest4j.drivers.wrk.script;

import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;
import org.loadtest4j.LoadTesterException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class WrkBodyVisitor implements Body.Visitor<String> {

    private static final String BODY_BOUNDARY = "--" + MultipartBoundary.STANDARD;
    private static final String CRLF = "\r\n";
    private static final String BOTTOM_BOUNDARY = "--" + MultipartBoundary.STANDARD + "--";

    @Override
    public String string(String body) {
        return body;
    }

    @Override
    public String parts(List<BodyPart> body) {
        final StringBuilder sb = new StringBuilder();
        for (BodyPart bodyPart: body) {
            final BodyPartVisitorResult result = bodyPart.accept(new WrkBodyPartVisitor());
            final String contentDisposition = result.contentDisposition;
            final String content = result.content;

            sb.append(BODY_BOUNDARY)
                    .append(CRLF)
                    .append(contentDisposition)
                    .append(CRLF)
                    .append(CRLF)
                    .append(content)
                    .append(CRLF);
        }
        sb.append(BOTTOM_BOUNDARY);

        return sb.toString();
    }

    private static class WrkBodyPartVisitor implements BodyPart.Visitor<BodyPartVisitorResult> {

        @Override
        public BodyPartVisitorResult stringPart(String name, String content) {
            final String contentDisposition = "Content-Disposition: form-data; name=\"" + name + "\"";
            return new BodyPartVisitorResult(content, contentDisposition);
        }

        @Override
        public BodyPartVisitorResult filePart(Path file) {
            final String name = Optional.ofNullable(file.getFileName()).orElseThrow(NullPointerException::new).toString();
            final String contentDisposition = "Content-Disposition: form-data; filename=\"" + name + "\"";
            final String content = readFileToString(file);
            return new BodyPartVisitorResult(content, contentDisposition);
        }
    }

    private static class BodyPartVisitorResult {

        private final String content;
        private final String contentDisposition;

        private BodyPartVisitorResult(String content, String contentDisposition) {
            this.content = content;
            this.contentDisposition = contentDisposition;
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
