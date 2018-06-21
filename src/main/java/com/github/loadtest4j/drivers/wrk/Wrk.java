package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.loadtest4j.DriverRequest;
import com.github.loadtest4j.loadtest4j.Driver;
import com.github.loadtest4j.loadtest4j.LoadTesterException;
import com.github.loadtest4j.loadtest4j.DriverResult;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * Runs a load test using the 'wrk' program by Will Glozer (https://github.com/wg/wrk).
 */
class Wrk implements Driver {

    private static final LoadTesterException WRK_OUTPUT_MALFORMATTED_EXCEPTION = new LoadTesterException("The output from wrk was malformatted.");

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

        final WrkLuaScript script = new WrkLuaScript(requests);

        try (AutoDeletingTempFile scriptPath = AutoDeletingTempFile.create(script.toString())) {
            final List<String> arguments = new ArgumentBuilder()
                    .addNamedArgument("--connections", valueOf(connections))
                    .addNamedArgument("--duration", String.format("%ds", duration.getSeconds()))
                    .addNamedArgument("--script", scriptPath.getAbsolutePath())
                    .addNamedArgument("--threads", valueOf(threads))
                    .addArgument(url)
                    .build();

            final Command command = new Command(arguments, executable);

            final Process process = new Shell().start(command);

            final int exitStatus = process.run();

            final String wrkReport = process.readStdout();

            if (exitStatus != 0) throw new LoadTesterException("Command exited with an error");

            final URI wrkReportUri = writeReport(wrkReport);

            return toDriverResult(wrkReport, wrkReportUri);
        }
    }

    private static <T> void validateNotEmpty(Collection<T> requests) {
        if (requests.size() < 1) {
            throw new LoadTesterException("No requests were specified for the load test.");
        }
    }

    private static URI writeReport(String report) {
        try {
            final File reportFile = File.createTempFile("wrk", "txt");

            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reportFile), StandardCharsets.UTF_8))) {
                writer.write(report);
            }

            return reportFile.toPath().toUri();
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    private static DriverResult toDriverResult(String report, URI reportUri) {
        final long ko = Regex.compile("Non-2xx or 3xx responses: (\\d+)")
                .firstMatch(report)
                .map(Long::parseLong)
                .orElse(0L);

        final long requests = Regex.compile("(\\d+) requests in ")
                .firstMatch(report)
                .map(Long::parseLong)
                .orElseThrow(() -> WRK_OUTPUT_MALFORMATTED_EXCEPTION);

        final Duration actualDuration = Regex.compile(" requests in (.+),")
                .firstMatch(report)
                .map(WrkDuration::parse)
                .orElseThrow(() -> WRK_OUTPUT_MALFORMATTED_EXCEPTION);

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
