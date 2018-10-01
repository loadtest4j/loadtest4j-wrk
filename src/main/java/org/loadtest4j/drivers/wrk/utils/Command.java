package org.loadtest4j.drivers.wrk.utils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Command {

    private final List<String> arguments;
    private final Map<String, String> env;
    private final String launchPath;

    public Command(List<String> arguments, Map<String, String> env, String launchPath) {
        this.arguments = arguments;
        this.env = env;
        this.launchPath = launchPath;
    }

    public List<String> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public String getLaunchPath() {
        return launchPath;
    }
}
