package org.loadtest4j.drivers.wrk.script;

import org.junit.experimental.categories.Category;
import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;
import org.loadtest4j.drivers.wrk.junit.UnitTest;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class WrkBodyMatcherTest extends BodyMatcherTest {
    @Override
    public void testString() {
        final WrkBodyMatcher matcher = new WrkBodyMatcher();

        final String body = Body.string("foo").match(matcher);

        assertThat(body).isEqualTo("foo");
    }

    @Override
    public void testStringPart() {
        final WrkBodyMatcher matcher = new WrkBodyMatcher();

        final String body = Body.multipart(BodyPart.string("foo", "bar")).match(matcher);

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
        final WrkBodyMatcher matcher = new WrkBodyMatcher();

        final String body = Body.multipart(BodyPart.file(Paths.get("src/test/resources/fixtures/multipart/test.txt"))).match(matcher);

        assertThat(body).isEqualTo(m(
                "--coBUzDU0QjP5Lc8yTgVGwB_j7FIBUJO9U8x",
                "\r\n",
                "Content-Disposition: form-data; filename=\"test.txt\"",
                "\r\n",
                "Content-Type: text/plain",
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
