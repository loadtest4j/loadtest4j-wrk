package com.github.loadtest4j.drivers.wrk;

import com.github.loadtest4j.drivers.wrk.junit.IntegrationTest;
import com.github.loadtest4j.loadtest4j.Driver;
import com.github.loadtest4j.loadtest4j.DriverRequest;
import com.github.loadtest4j.loadtest4j.DriverResult;
import com.github.loadtest4j.loadtest4j.LoadTesterException;
import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.get;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class WrkTest {

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
        return new Wrk(1, Duration.ofSeconds(1), executable, 1, getServiceUrl());
    }

    @Test
    public void testRun()  {
        // Given
        final Driver driver = sut();
        // And
        whenHttp(httpServer).match(get("/")).then(status(HttpStatus.OK_200));

        // When
        final Collection<DriverRequest> requests = Collections.singletonList(DriverRequests.get("/"));
        final DriverResult result = driver.run(requests);

        // Then
        assertTrue(result.getRequests() >= 0);
        assertEquals(0, result.getErrors());
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
        final Collection<DriverRequest> requests = Arrays.asList(DriverRequests.get("/"), DriverRequests.get("/pets"));
        final DriverResult result = driver.run(requests);

        // Then
        assertTrue(result.getRequests() >= 0);
        assertEquals(0, result.getErrors());
    }

    @Test
    public void testRunWithNoRequests() {
        // Given
        final Driver driver = sut();

        // When
        try {
            driver.run(Collections.emptyList());

            fail("This should not work.");
        } catch (LoadTesterException e) {
            // Then
            assertEquals("No requests were specified for the load test.", e.getMessage());
        }
    }
}
