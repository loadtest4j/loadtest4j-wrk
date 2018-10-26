package org.loadtest4j.drivers.wrk;

import org.loadtest4j.LoadTesterException;
import org.loadtest4j.driver.Driver;
import org.loadtest4j.driver.DriverRequest;
import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.driver.DriverResult;
import org.loadtest4j.drivers.wrk.dto.*;
import org.loadtest4j.drivers.wrk.script.WrkBodyMatcher;
import org.loadtest4j.drivers.wrk.script.WrkHeadersMatcher;
import org.loadtest4j.drivers.wrk.utils.*;
import org.loadtest4j.drivers.wrk.utils.Process;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
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

        final Path input = createInput(requests);

        return runWrkViaShell(input);
    }

    private static <T> void validateNotEmpty(Collection<T> requests) {
        if (requests.size() < 1) {
            throw new LoadTesterException("No requests were specified for the load test.");
        }
    }

    private static Path createInput(List<DriverRequest> requests) {
        final List<Req> wrkRequests = wrkRequests(requests);
        final Input input = new Input(wrkRequests);
        final Path inputPath = FileUtils.createTempFile("loadtest4j-wrk", ".json");
        try {
            Json.serialize(inputPath.toFile(), input);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
        return inputPath;
    }

    private DriverResult runWrkViaShell(Path input) {
        final Path luaScript = createLuaScript();
        final Path luaOutput = FileUtils.createTempFile("loadtest4j-output", ".json");

        final List<String> arguments = new ArgumentBuilder()
                .addNamedArgument("--connections", valueOf(connections))
                .addNamedArgument("--duration", String.format("%ds", duration.getSeconds()))
                .addNamedArgument("--script", luaScript.toString())
                .addNamedArgument("--threads", valueOf(threads))
                .addArgument(url)
                .addArgument(input.toString())
                .build();

        final Map<String, String> env = Collections.singletonMap("WRK_OUTPUT", luaOutput.toString());

        final Command command = new Command(arguments, env, executable);

        final Process process = new Shell().start(command);

        // Reduce sleep/wakeup cycles waiting for process by doing the sleep ourselves
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            // It was worth a try - just fall back to process.waitFor()
        }

        final int exitStatus = process.waitFor();

        if (exitStatus != 0) {
            final String error = StreamReader.streamToString(process.getStderr());
            throw new LoadTesterException("Wrk error:\n\n" + error);
        }

        try {
            return toDriverResult(luaOutput.toAbsolutePath());
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    private static DriverResult toDriverResult(Path path) throws IOException {
        return toDriverResult(new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.UTF_8));
    }

    static DriverResult toDriverResult(Reader report) throws IOException {
        final Output output = Json.parse(report, Output.class);
        return toDriverResult(output);
    }

    private static DriverResult toDriverResult(Output output) {
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

        final DriverResponseTime responseTime = new WrkResponseTime(output.getLatency().getPercentiles());

        return new WrkResult(ok, ko, actualDuration, responseTime);
    }

    private static Path createLuaScript() {
        final InputStream scriptStream = Wrk.class.getResourceAsStream("/loadtest4j-wrk.lua");
        final Path script = FileUtils.createTempFile("loadtest4j-wrk", ".lua");
        FileUtils.copy(scriptStream, script);
        return script;
    }

    private static List<Req> wrkRequests(List<DriverRequest> requests) {
        return requests.stream()
                .map(Wrk::wrkRequest)
                .collect(Collectors.toList());
    }

    private static Req wrkRequest(DriverRequest request) {
        final String body = request.getBody().match(new WrkBodyMatcher());
        final Map<String, String> headers = request.getBody().match(new WrkHeadersMatcher(request.getHeaders()));
        final String method = request.getMethod();
        final String path = request.getPath() + QueryString.fromMap(request.getQueryParams());

        return new Req(body, headers, method, path);
    }
}
