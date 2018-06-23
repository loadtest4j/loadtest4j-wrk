package com.github.loadtest4j.drivers.wrk.shell;

import java.util.Collections;
import java.util.List;

class Command {

    private final List<String> arguments;
    private final String launchPath;

    Command(List<String> arguments, String launchPath) {
        this.arguments = arguments;
        this.launchPath = launchPath;
    }

    protected List<String> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    protected String getLaunchPath() {
        return launchPath;
    }
}