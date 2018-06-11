package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.drivers.wrk.junit.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@Category(UnitTest.class)
public class QueryStringTest {

    @Test
    public void testQueryString() {
        final QueryString queryString = new QueryString(Collections.singletonMap("foo", "1"));

        assertEquals("?foo=1", queryString.toString());
    }

    @Test
    public void testQueryStringWithMultipleParams() {
        final Map<String, String> queryParams = new LinkedHashMap<String, String>() {{
            put("foo", "1");
            put("bar", "2");
        }};

        final QueryString queryString = new QueryString(queryParams);

        assertEquals("?foo=1&bar=2", queryString.toString());
    }
}
