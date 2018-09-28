package org.loadtest4j.drivers.wrk;

import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.driver.Driver;
import org.loadtest4j.driver.DriverRequest;
import org.loadtest4j.driver.DriverResult;
import org.loadtest4j.drivers.wrk.junit.DriverResultAssert;
import org.loadtest4j.drivers.wrk.junit.IntegrationTest;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.*;

@Category(IntegrationTest.class)
public class WrkTest {

    private static final Duration EXPECTED_DURATION = Duration.ofSeconds(1);

    private StubServer httpServer;

    static {
        // Silence Restito logging.
        Logger.getLogger("org.glassfish.grizzly").setLevel(Level.OFF);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void startServer() {
        httpServer = new StubServer().run();
    }

    @After
    public void stopServer() {
        httpServer.stop();
    }

    private String getServiceUrl() {
        return String.format("http://localhost:%d", httpServer.getPort());
    }

    private Driver sut() {
        final String executable = "wrk";
        return new Wrk(1, EXPECTED_DURATION, executable, 1, getServiceUrl());
    }

    @Test
    public void testRun()  {
        // Given
        final Driver driver = sut();
        // And
        whenHttp(httpServer).match(get("/")).then(status(HttpStatus.OK_200));

        // When
        final List<DriverRequest> requests = Collections.singletonList(DriverRequests.get("/"));
        final DriverResult result = driver.run(requests);

        // Then
        DriverResultAssert.assertThat(result)
                .hasOkGreaterThan(0)
                .hasKo(0)
                .hasActualDurationGreaterThan(EXPECTED_DURATION)
                .hasMaxResponseTimeGreaterThan(Duration.ZERO);
    }

    @Test
    public void testRunWithMultipleRequests() {
        // Given
        final Driver driver = sut();
        // And
        whenHttp(httpServer).match(get("/")).then(status(HttpStatus.OK_200));
        // And
        whenHttp(httpServer).match(get("/pets")).then(status(HttpStatus.OK_200));

        // When
        final List<DriverRequest> requests = Arrays.asList(DriverRequests.get("/"), DriverRequests.get("/pets"));
        final DriverResult result = driver.run(requests);

        // Then
        DriverResultAssert.assertThat(result)
                .hasOkGreaterThan(0)
                .hasKo(0);
    }

    @Test
    public void testRunWithJsonPost() {
        // Given
        final Driver driver = sut();
        // And
        final String body = "{" + "\n"
                + "\"three\": \"bott\\les\"" + "\n"
                + "}";
        // And
        whenHttp(httpServer).match(post("/pets"), withPostBodyContaining(body)).then(status(HttpStatus.OK_200));

        // When
        final DriverRequest edgeCaseReq = new DriverRequest(body, Collections.emptyMap(),"POST","/pets", Collections.emptyMap());
        final List<DriverRequest> requests = Collections.singletonList(edgeCaseReq);
        final DriverResult result = driver.run(requests);

        // Then
        DriverResultAssert.assertThat(result)
                .hasOkGreaterThan(0)
                .hasKo(0);
    }

    @Test
    public void testRunWithPostBodyContainingEscapedDoubleQuotes() {
        // Given
        final Driver driver = sut();
        // And
        final String body = "a \\\"badly escaped\\\" string";
        // And
        whenHttp(httpServer).match(post("/pets"), withPostBodyContaining(body)).then(status(HttpStatus.OK_200));

        // When
        final DriverRequest edgeCaseReq = new DriverRequest(body, Collections.emptyMap(),"POST","/pets", Collections.emptyMap());
        final List<DriverRequest> requests = Collections.singletonList(edgeCaseReq);
        final DriverResult result = driver.run(requests);

        // Then
        DriverResultAssert.assertThat(result)
                .hasOkGreaterThan(0)
                .hasKo(0);
    }

    @Test
    public void testRunWithEdgeCaseRequest() {
        // Given
        final Driver driver = sut();
        // And
        whenHttp(httpServer)
                .match(post("/pets"), withHeader("fo'o", "ba'r"), withPostBodyContaining("three\ngreen\nbottles"))
                .then(status(HttpStatus.OK_200));

        // When
        final DriverRequest edgeCaseReq = new DriverRequest("three\ngreen\nbottles",
                Collections.singletonMap("fo'o", "ba'r"),
                "POST",
                "/pets",
                Collections.emptyMap());
        final List<DriverRequest> requests = Collections.singletonList(edgeCaseReq);
        final DriverResult result = driver.run(requests);

        // Then
        DriverResultAssert.assertThat(result)
                .hasOkGreaterThan(0)
                .hasKo(0);
    }

    @Test
    public void testRunWithErrors()  {
        // Given
        final Driver driver = sut();
        // And
        whenHttp(httpServer).match(get("/")).then(status(HttpStatus.OK_200));
        // And
        whenHttp(httpServer).match(get("/"), parameter("foo", "bar")).then(status(HttpStatus.NOT_FOUND_404));

        // When
        final List<DriverRequest> requests = Collections.singletonList(DriverRequests.getWithQueryParams("/", Collections.singletonMap("foo", "bar")));
        final DriverResult result = driver.run(requests);

        // Then
        DriverResultAssert.assertThat(result)
                .hasOk(0)
                .hasKoGreaterThan(0);
    }

    @Test
    public void testRunWithInvalidHost() {
        // Given
        final Driver driver = new Wrk(1, EXPECTED_DURATION, "wrk", 1, "http://localhost:1");

        // Expect
        thrown.expect(LoadTesterException.class);
        thrown.expectMessage("Wrk error");

        // When
        final List<DriverRequest> requests = Collections.singletonList(DriverRequests.getWithQueryParams("/", Collections.singletonMap("foo", "bar")));
        driver.run(requests);
    }


    @Test
    public void testRunWithNoRequests() {
        // Given
        final Driver driver = sut();

        // Expect
        thrown.expect(LoadTesterException.class);
        thrown.expectMessage("No requests were specified for the load test.");

        // When
        driver.run(Collections.emptyList());
    }
}
