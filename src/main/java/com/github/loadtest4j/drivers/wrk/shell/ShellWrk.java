package com.github.loadtest4j.drivers.wrk.shell;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.loadtest4j.drivers.wrk.shell.input.Input;
import com.github.loadtest4j.drivers.wrk.shell.output.Output;
import com.github.loadtest4j.loadtest4j.LoadTesterException;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * Low-level interface for wrk that runs it through the shell.
 */
public class ShellWrk {
    private final int connections;
    private final Duration duration;
    private final String executable;
    private final int threads;
    private final String url;

    public ShellWrk(int connections, Duration duration, String executable, int threads, String url) {
        this.connections = connections;
        this.duration = duration;
        this.executable = executable;
        this.threads = threads;
        this.url = url;
    }

    public Output run(Input input) {
        try (AutoDeletingTempFile luaScript = createLuaScript();
             AutoDeletingTempFile luaInput = createLuaInput(input)) {
            final List<String> arguments = new ArgumentBuilder()
                    .addNamedArgument("--connections", valueOf(connections))
                    .addNamedArgument("--duration", String.format("%ds", duration.getSeconds()))
                    .addNamedArgument("--script", luaScript.getAbsolutePath())
                    .addNamedArgument("--threads", valueOf(threads))
                    .addArgument(url)
                    .addArgument(luaInput.getAbsolutePath())
                    .build();

            final Command command = new Command(arguments, executable);

            final Process process = new Shell().start(command);

            final int exitStatus = process.run();

            final InputStream jsonReport = process.getStderr();
            final Output output = parse(jsonReport);

            if (exitStatus != 0) throw new LoadTesterException("Command exited with an error");

            return output;
        }
    }

    private static AutoDeletingTempFile createLuaInput(Input input) {
        final String serializedRequests = serialize(input);
        return AutoDeletingTempFile.create(serializedRequests);
    }

    private static String serialize(Input input) {
        try {
            return new ObjectMapper().writeValueAsString(input);
        } catch (JsonProcessingException e) {
            throw new LoadTesterException(e);
        }
    }

    private static AutoDeletingTempFile createLuaScript() {
        final InputStream scriptStream = ShellWrk.class.getResourceAsStream("/loadtest4j-wrk.lua");
        return AutoDeletingTempFile.create(scriptStream);
    }

    private static Output parse(InputStream json) {
        try {
            return new ObjectMapper().readValue(json, Output.class);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }
}
