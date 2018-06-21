package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.drivers.wrk.shell.input.Input;
import com.github.loadtest4j.drivers.wrk.shell.input.Req;
import com.github.loadtest4j.drivers.wrk.shell.output.Errors;
import com.github.loadtest4j.drivers.wrk.shell.output.Output;
import com.github.loadtest4j.drivers.wrk.shell.output.Summary;
import com.github.loadtest4j.drivers.wrk.report.LocalWrkReport;
import com.github.loadtest4j.drivers.wrk.shell.*;
import com.github.loadtest4j.loadtest4j.Driver;
import com.github.loadtest4j.loadtest4j.DriverRequest;
import com.github.loadtest4j.loadtest4j.DriverResult;
import com.github.loadtest4j.loadtest4j.LoadTesterException;

import java.net.URI;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        final ShellWrk shellWrk = new ShellWrk(connections, duration, executable, threads, url);

        final Input input = input(requests);

        final Output output = shellWrk.run(input);

        final URI wrkReportUri = writeOutput(output);

        return toDriverResult(output, wrkReportUri);
    }

    private static <T> void validateNotEmpty(Collection<T> requests) {
        if (requests.size() < 1) {
            throw new LoadTesterException("No requests were specified for the load test.");
        }
    }

    private static Input input(List<DriverRequest> requests) {
        return new Input(wrkRequests(requests));
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
        final String path = getPath(request);

        return new Req(body, headers, method, path);
    }

    private static String getPath(DriverRequest request) {
        return request.getPath() + getQueryString(request.getQueryParams());
    }

    private static String getQueryString(Map<String, String> queryParams) {
        return new QueryString(queryParams).toString();
    }

    private static URI writeOutput(Output output) {
        return new LocalWrkReport().save(output);
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
