package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.drivers.wrk.junit.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class LuaMapTest {
    @Test
    public void testSingleEntry() {
        final LuaMap map = new LuaMap(Collections.singletonMap("foo", "bar"));

        assertEquals("{['foo'] = 'bar'}", map.toString());
    }

    @Test
    public void testMultipleEntries() {
        final Map<String, String> multiMap = new LinkedHashMap<String, String>() {{
           put("foo", "1");
           put("bar", "2");
        }};

        final LuaMap map = new LuaMap(multiMap);

        assertEquals("{['foo'] = '1', ['bar'] = '2'}", map.toString());
    }

    @Test
    public void testEscaped() {
        final LuaMap map = new LuaMap(Collections.singletonMap("fo'o", "ba'r"));

        assertEquals("{['fo\\'o'] = 'ba\\'r'}", map.toString());
    }

    @Test
    public void testEmpty() {
        final LuaMap map = new LuaMap(Collections.emptyMap());

        assertEquals("{}", map.toString());
    }
}
