package org.loadtest4j.drivers.wrk.utils;

import org.loadtest4j.drivers.wrk.junit.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class QueryStringTest {

    @Test
    public void shouldMakeQueryString() {
        final String queryString = QueryString.fromMap(Collections.singletonMap("foo", "1"));

        assertThat(queryString).isEqualTo("?foo=1");
    }

    @Test
    public void shouldMakeQueryStringWithMultipleParams() {
        final Map<String, String> queryParams = new LinkedHashMap<String, String>() {{
            put("foo", "1");
            put("bar", "2");
        }};

        final String queryString = QueryString.fromMap(queryParams);

        assertThat(queryString).isEqualTo("?foo=1&bar=2");
    }
}
