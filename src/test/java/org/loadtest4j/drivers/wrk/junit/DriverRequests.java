package org.loadtest4j.drivers.wrk.junit;

import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;
import org.loadtest4j.driver.DriverRequest;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public class DriverRequests {
    public static DriverRequest get(String path) {
        return new DriverRequest(Body.string(""), Collections.emptyMap(), "GET", path, Collections.emptyMap());
    }

    public static DriverRequest getWithQueryParams(String path, Map<String, String> queryParams) {
        return new DriverRequest(Body.string(""), Collections.emptyMap(), "GET", path, queryParams);
    }

    public static DriverRequest uploadMultiPart(String path, Path a, Path b, Map<String, String> headers) {
        final Body body = Body.multipart(BodyPart.file(a), BodyPart.file(b));
        return new DriverRequest(body, headers, "POST", path, Collections.emptyMap());
    }

    public static DriverRequest uploadMultiPart(String path, String a, String aContent, String b, String bContent, Map<String, String> headers) {
        final Body body = Body.multipart(BodyPart.string(a, aContent), BodyPart.string(b, bContent));
        return new DriverRequest(body, headers, "POST", path, Collections.emptyMap());
    }
}
