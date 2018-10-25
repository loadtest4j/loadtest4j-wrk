package org.loadtest4j.drivers.wrk.script;

import org.junit.experimental.categories.Category;
import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;
import org.loadtest4j.drivers.wrk.junit.UnitTest;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class WrkHeadersVisitorTest extends BodyVisitorTest {
    @Override
    public void testString() {
        final WrkHeadersVisitor visitor = new WrkHeadersVisitor(Collections.emptyMap());

        final Map<String, String> headers = Body.string("foo").accept(visitor);

        assertThat(headers).isEmpty();
    }

    @Override
    public void testStringPart() {
        final WrkHeadersVisitor visitor = new WrkHeadersVisitor(Collections.emptyMap());

        final Map<String, String> headers = Body.parts(BodyPart.string("foo", "bar")).accept(visitor);

        assertThat(headers).containsEntry("Content-Type", "multipart/form-data; boundary=coBUzDU0QjP5Lc8yTgVGwB_j7FIBUJO9U8x");
    }

    @Override
    public void testFilePart() {
        final WrkHeadersVisitor visitor = new WrkHeadersVisitor(Collections.emptyMap());

        final Map<String, String> headers = Body.parts(BodyPart.file(Paths.get("/tmp/foo.txt"))).accept(visitor);

        assertThat(headers).containsEntry("Content-Type", "multipart/form-data; boundary=coBUzDU0QjP5Lc8yTgVGwB_j7FIBUJO9U8x");
    }
}
