package com.github.loadtest4j.drivers.wrk;

import java.util.Map;
import java.util.stream.Collectors;

class QueryString {
    private final Map<String, String> queryParams;

    QueryString(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    @Override
    public String toString() {
        if (queryParams.isEmpty()) {
            return "";
        }

        return "?" + queryParams.entrySet()
                .stream()
                .map((entry) -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("&"));
    }
}
