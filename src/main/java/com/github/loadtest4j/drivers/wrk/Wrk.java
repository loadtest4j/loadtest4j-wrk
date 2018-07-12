package com.github.loadtest4j.drivers.wrk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.loadtest4j.drivers.wrk.dto.*;
import com.github.loadtest4j.drivers.wrk.utils.*;
import com.github.loadtest4j.drivers.wrk.utils.Process;
import com.github.loadtest4j.loadtest4j.LoadTesterException;
import com.github.loadtest4j.loadtest4j.ResponseTime;
import com.github.loadtest4j.loadtest4j.driver.Driver;
import com.github.loadtest4j.loadtest4j.driver.DriverRequest;
import com.github.loadtest4j.loadtest4j.driver.DriverResult;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        final String input = createInput(requests);

        final URL report = runWrkViaShell(input);

        return toDriverResult(report);
    }

    private static <T> void validateNotEmpty(Collection<T> requests) {
        if (requests.size() < 1) {
            throw new LoadTesterException("No requests were specified for the load test.");
        }
    }

    private static String createInput(List<DriverRequest> requests) {
        final List<Req> wrkRequests = wrkRequests(requests);
        final Input input = new Input(wrkRequests);
        try {
            return new ObjectMapper().writeValueAsString(input);
        } catch (JsonProcessingException e) {
            throw new LoadTesterException(e);
        }
    }

    private URL runWrkViaShell(String input) {
        // Takes raw input, gives raw output. Makes no attempt to understand the contents.
        try (AutoDeletingTempFile luaScript = createLuaScript();
             AutoDeletingTempFile luaInput = AutoDeletingTempFile.create(input)) {
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

            final String report = StreamReader.streamToString(process.getStderr());

            final int exitStatus = process.waitFor();

            if (exitStatus != 0) throw new LoadTesterException("Wrk error:\n\n" + report);

            return writeReport(report);
        }
    }

    private static URL writeReport(String report) {
        try {
            final File file = File.createTempFile("wrk", "json");

            try (Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
                w.write(report);
            }

            return file.toPath().toUri().toURL();
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    protected static DriverResult toDriverResult(URL reportUrl) {
        try {
            final Output output = new ObjectMapper().readValue(reportUrl, Output.class);
            return toDriverResult(output, reportUrl);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    private static DriverResult toDriverResult(Output output, URL reportUrl) {
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
        // This means that in wrk parlance...
        // 'requests' = the total number of requests, not the number of OK requests.
        // 'errors' = HTTP status errors, not including any socket errors.
        final long ok = requests - errors.getStatus();

        final ResponseTime responseTime = new WrkResponseTime(output.getLatency().getPercentiles());

        return new WrkResult(ok, ko, actualDuration, responseTime, reportUrl.toString());
    }

    // FIXME extras

    private static AutoDeletingTempFile createLuaScript() {
        final InputStream scriptStream = Wrk.class.getResourceAsStream("/loadtest4j-wrk.lua");
        return AutoDeletingTempFile.create(scriptStream);
    }

    private static List<Req> wrkRequests(List<DriverRequest> requests) {
        return requests.stream()
                .map(Wrk::wrkRequest)
                .collect(Collectors.toList());
    }

    private static Req wrkRequest(DriverRequest request) {
        final String body = request.getBody();
        final Map<String, String> headers = request.getHeaders();
        final String method = request.getMethod();
        final String path = request.getPath() + QueryString.fromMap(request.getQueryParams());

        return new Req(body, headers, method, path);
    }
}
