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
import java.util.Optional;

import static java.lang.String.valueOf;

/**
 * Runs a load test using the 'wrk' program by Will Glozer (https://github.com/wg/wrk).
 */
class Wrk implements Driver {

    private static final String ACTUAL_DURATION = "actualDuration";
    private static final String KO = "ko";
    private static final String MAX = "max";
    private static final String P50 = "p50";
    private static final String P75 = "p75";
    private static final String P90 = "p90";
    private static final String P99 = "p99";
    private static final String REQUESTS = "requests";
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
                    .addArgument("--latency")
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
        final String reg = lines(
                indent() + tabbed("Thread Stats", "Avg", "Stdev", "Max", "\\+\\/- Stdev"),
                indent() + tabbed("Latency", skip(), skip(), capture(MAX, duration()), skip()),
                skipLine(),
                indent() + "Latency Distribution",
                // FIXME wrk2 has decimals here
                indent() + tabbed("50%", capture(P50, duration())),
                indent() + tabbed("75%", capture(P75, duration())),
                indent() + tabbed("90%", capture(P90, duration())),
                indent() + tabbed("99%", capture(P99, duration())),
                indent() + capture(REQUESTS, integer()) + " requests in " + capture(ACTUAL_DURATION, duration()) + "," + skipLine());

        final Regex r = Regex.compile(reg);
        final Regex.Matcher matcher = r.match(report);

        final Optional<Duration> p50 = matcher.group(P50)
                .map(WrkDuration::parse);

        final Optional<Duration> p75 = matcher.group(P75)
                .map(WrkDuration::parse);

        final Optional<Duration> p90 = matcher.group(P90)
                .map(WrkDuration::parse);

        final Optional<Duration> p99 = matcher.group(P99)
                .map(WrkDuration::parse);

        final Optional<Duration> max = matcher.group(MAX)
                .map(WrkDuration::parse);

        final long requests = matcher.group(REQUESTS)
                .map(Long::parseLong)
                .orElseThrow(() -> WRK_OUTPUT_MALFORMATTED_EXCEPTION);

        final Duration actualDuration = matcher.group(ACTUAL_DURATION)
                .map(WrkDuration::parse)
                .orElseThrow(() -> WRK_OUTPUT_MALFORMATTED_EXCEPTION);

        final Regex failure = Regex.compile("Non-2xx or 3xx responses: " + capture(KO, integer()));
        final Regex.Matcher failureMatcher = failure.match(report);
        final long ko = failureMatcher.group(KO)
                .map(Long::parseLong)
                .orElse(0L);

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

    private static String tabbed(String... parts) {
        return String.join("\\s+", parts);
    }

    private static String indent() {
        return "\\s+";
    }

    private static String skip() {
        return "[\\w.%]+";
    }

    private static String skipLine() {
        return ".+";
    }

    private static String capture(String name, String pattern) {
        return "(?<" + name + ">" + pattern + ")";
    }

    private static String duration() {
        return ".\\S+";
    }

    private static String integer() {
        return "\\d+";
    }

    private static String lines(String... lines) {
        return String.join("\\n", lines);
    }
}
