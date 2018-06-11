package com.github.loadtest4j.drivers.wrk;

class LuaMultiLineString {
    private final String raw;

    LuaMultiLineString(String raw) {
        this.raw = raw;
    }

    @Override
    public String toString() {
        // Ensure the Lua long-format string delimiter is not included in the string.
        final String escaped = raw
                .replace("[", "\\[")
                .replace("]", "\\]");

        // Wrap the string in the Lua long-format string delimiter
        return "[[" + escaped + "]]";
    }
}
