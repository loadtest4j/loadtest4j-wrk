package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.drivers.wrk.junit.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class LuaMultiLineStringTest {
    @Test
    public void testSimple() {
        final LuaMultiLineString str = new LuaMultiLineString("foo bar");

        assertEquals("[[foo bar]]", str.toString());
    }

    @Test
    public void testMultiline() {
        final String raw = String.join("\n", "four", "score", "and", "seven", "years", "ago");

        final LuaMultiLineString str = new LuaMultiLineString(raw);

        final String expected = "[[" + "four" + "\n"
                + "score" + "\n"
                + "and" + "\n"
                + "seven" + "\n"
                + "years" + "\n"
                + "ago" + "]]";

        assertEquals(expected, str.toString());
    }

    @Test
    public void testSingleStartIndicatorEscaped() {
        final LuaMultiLineString str = new LuaMultiLineString("foo [ bar");

        assertEquals("[[foo \\[ bar]]", str.toString());
    }

    @Test
    public void testDoubleStartIndicatorEscaped() {
        final LuaMultiLineString str = new LuaMultiLineString("foo [[ bar");

        assertEquals("[[foo \\[\\[ bar]]", str.toString());
    }

    @Test
    public void testSingleEndIndicatorEscaped() {
        final LuaMultiLineString str = new LuaMultiLineString("foo ] bar");

        assertEquals("[[foo \\] bar]]", str.toString());
    }

    @Test
    public void testDoubleEndIndicatorEscaped() {
        final LuaMultiLineString str = new LuaMultiLineString("foo ]] bar");

        assertEquals("[[foo \\]\\] bar]]", str.toString());
    }
}
