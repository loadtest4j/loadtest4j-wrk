package org.loadtest4j.drivers.wrk.utils;

import org.loadtest4j.LoadTesterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Shell {

    public org.loadtest4j.drivers.wrk.utils.Process start(Command command) {
        final List<String> cmd = new ArrayList<>();
        cmd.add(command.getLaunchPath());
        cmd.addAll(command.getArguments());

        final ProcessBuilder pb = new ProcessBuilder(cmd)
                .redirectInput(ProcessBuilder.Redirect.PIPE);
        pb.environment().putAll(command.getEnv());
        try {
            return new org.loadtest4j.drivers.wrk.utils.Process(pb.start());
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

}
