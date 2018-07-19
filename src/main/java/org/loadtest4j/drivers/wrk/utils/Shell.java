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

        try {
            return new org.loadtest4j.drivers.wrk.utils.Process(new ProcessBuilder(cmd).redirectInput(ProcessBuilder.Redirect.PIPE).start());
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

}
