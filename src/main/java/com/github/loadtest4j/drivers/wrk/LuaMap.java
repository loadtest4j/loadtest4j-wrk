package com.github.loadtest4j.drivers.wrk;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

class LuaMap extends AbstractMap<String, String> {
    private final Map<String, String> map;

    LuaMap(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return map.entrySet();
    }

    @Override
    public String toString() {
        return luaMap(map);
    }

    private static String luaMap(Map<String, String> map) {
        final StringJoiner sj = new StringJoiner(", ");
        map.forEach((k, v) -> sj.add(luaMapEntry(k, v)));
        return "{" + sj.toString() + "}";
    }

    private static String luaMapEntry(String k, String v) {
        final String escapedKey = escapeSingleQuotes(k);
        final String escapedValue = escapeSingleQuotes(v);
        return String.format("['%s'] = '%s'", escapedKey, escapedValue);
    }

    private static String escapeSingleQuotes(String str) {
        return str.replace("'", "\\'");
    }
}
