package org.loadtest4j.drivers.wrk.script;

import org.junit.experimental.categories.Category;
import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;
import org.loadtest4j.drivers.wrk.junit.UnitTest;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class WrkBodyVisitorTest extends BodyVisitorTest {
    @Override
    public void testString() {
        final WrkBodyVisitor visitor = new WrkBodyVisitor();

        final String body = Body.string("foo").accept(visitor);

        assertThat(body).isEqualTo("foo");
    }

    @Override
    public void testStringPart() {
        final WrkBodyVisitor visitor = new WrkBodyVisitor();

        final String body = Body.parts(BodyPart.string("foo", "bar")).accept(visitor);

        assertThat(body).isEqualTo(m(
                "--coBUzDU0QjP5Lc8yTgVGwB_j7FIBUJO9U8x",
                "\r\n",
                "Content-Disposition: form-data; name=\"foo\"",
                "\r\n",
                "\r\n",
                "bar",
                "\r\n",
                "--coBUzDU0QjP5Lc8yTgVGwB_j7FIBUJO9U8x--"));
    }

    @Override
    public void testFilePart() {
        final WrkBodyVisitor visitor = new WrkBodyVisitor();

        final String body = Body.parts(BodyPart.file(Paths.get("src/test/resources/fixtures/multipart/test.txt"))).accept(visitor);

        assertThat(body).isEqualTo(m(
                "--coBUzDU0QjP5Lc8yTgVGwB_j7FIBUJO9U8x",
                "\r\n",
                "Content-Disposition: form-data; filename=\"test.txt\"",
                "\r\n",
                "\r\n",
                "foo",
                "\r\n",
                "--coBUzDU0QjP5Lc8yTgVGwB_j7FIBUJO9U8x--"));
    }

    private static String m(String... lines) {
        return String.join("", lines);
    }
}
