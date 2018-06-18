package com.github.loadtest4j.drivers.wrk;

import java.util.Optional;
import java.util.regex.Pattern;

class Regex {

    private final Pattern pattern;

    private Regex(Pattern pattern) {
        this.pattern = pattern;
    }

    protected Matcher match(String str) {
        final java.util.regex.Matcher matcher = pattern.matcher(str);
        matcher.find();
        return new Matcher(matcher);
    }

    protected static Regex compile(String pattern) {
        return new Regex(Pattern.compile(pattern));
    }

    static class Matcher {

        private final java.util.regex.Matcher matcher;

        private Matcher(java.util.regex.Matcher matcher) {
            this.matcher = matcher;
        }

        protected Optional<String> group(String name) {
            try {
                final String text = matcher.group(name);
                return Optional.of(text);
            } catch (IllegalStateException | IllegalArgumentException e) {
                return Optional.empty();
            }
        }
    }
}

