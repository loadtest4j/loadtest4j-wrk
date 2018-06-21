package com.github.loadtest4j.drivers.wrk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.loadtest4j.drivers.wrk.output.Errors;
import com.github.loadtest4j.drivers.wrk.output.Output;
import com.github.loadtest4j.drivers.wrk.output.Summary;
import com.github.loadtest4j.loadtest4j.Driver;
import com.github.loadtest4j.loadtest4j.DriverRequest;
import com.github.loadtest4j.loadtest4j.DriverResult;
import com.github.loadtest4j.loadtest4j.LoadTesterException;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Collection;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * Runs a load test using the 'wrk' program by Will Glozer (https://github.com/wg/wrk).
 */
class Wrk implements Driver {

    private final int connections;
    private final Duration duration;
    private final String executable;
    private final int threads;
    private final String url;

    Wrk(int connections, Duration duration, String executable, int threads, String url) {
        this.connections = connections;
        this.duration = duration;
        this.executable = executable;
        this.threads = threads;
        this.url = url;
    }

    @Override
    public DriverResult run(List<DriverRequest> requests) {
        validateNotEmpty(requests);

        try (AutoDeletingTempFile script = WrkLuaScript.create();
             AutoDeletingTempFile input = WrkLuaInput.create(requests)) {
            final List<String> arguments = new ArgumentBuilder()
                    .addNamedArgument("--connections", valueOf(connections))
                    .addNamedArgument("--duration", String.format("%ds", duration.getSeconds()))
                    .addNamedArgument("--script", script.getAbsolutePath())
                    .addNamedArgument("--threads", valueOf(threads))
                    .addArgument(url)
                    .addArgument(input.getAbsolutePath())
                    .build();

            final Command command = new Command(arguments, executable);

            final Process process = new Shell().start(command);

            final int exitStatus = process.run();

            final InputStream wrkReport = process.getStdout();
            final InputStream jsonReport = process.getStderr();
            final Output output = parse(jsonReport);

            if (exitStatus != 0) throw new LoadTesterException("Command exited with an error");

            final URI wrkReportUri = writeReport(wrkReport);

            return toDriverResult(output, wrkReportUri);
        }
    }

    private static <T> void validateNotEmpty(Collection<T> requests) {
        if (requests.size() < 1) {
            throw new LoadTesterException("No requests were specified for the load test.");
        }
    }

    private static URI writeReport(InputStream report) {
        try {
            final File reportFile = File.createTempFile("wrk", "txt");

            Files.copy(report, reportFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return reportFile.toPath().toUri();
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    private static Output parse(InputStream json) {
        try {
            return new ObjectMapper().readValue(json, Output.class);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    private static DriverResult toDriverResult(Output output, URI reportUri) {
        final Summary summary = output.getSummary();

        final Errors errors = summary.getErrors();

        final long ko = errors.getConnect() + errors.getRead() + errors.getStatus() + errors.getTimeout() + errors.getWrite();

        final long requests = summary.getRequests();

        final Duration actualDuration = Duration.ofNanos(summary.getDuration() * 1000);

        // When wrk runs and a test completely fails, e.g. against a URL which does not exist, we see output like:
        //
        // 16 requests in 2.06s, 5.50KB read
        //  Non-2xx or 3xx responses: 16
        //
        // This means that in wrk parlance, 'requests' = the total number of requests, not the number of OK requests
        final long ok = requests - ko;

        final String reportUrl = reportUri.toString();

        return new WrkResult(ok, ko, actualDuration, reportUrl);
    }
}
