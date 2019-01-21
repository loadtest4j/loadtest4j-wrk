# loadtest4j-wrk

[![Build Status](https://travis-ci.com/loadtest4j/loadtest4j-wrk.svg?branch=master)](https://travis-ci.com/loadtest4j/loadtest4j-wrk)
[![Codecov](https://codecov.io/gh/loadtest4j/loadtest4j-wrk/branch/master/graph/badge.svg)](https://codecov.io/gh/loadtest4j/loadtest4j-wrk)
[![Maven Central](https://img.shields.io/maven-central/v/org.loadtest4j.drivers/loadtest4j-wrk.svg)](http://repo2.maven.org/maven2/org/loadtest4j/drivers/loadtest4j-wrk/)

Wrk driver for [loadtest4j](https://www.loadtest4j.org).

## Prerequisites

**Install the `wrk` executable** on your `$PATH`:

- Homebrew: run `brew install wrk`.
- Compile-from-source: check out the instructions in [`.travis.yml`](.travis.yml).

## Usage

With a new or existing Maven project open in your favorite editor...

### 1. Add the library

Add the library to your Maven project POM.

```xml
<dependency>
    <groupId>org.loadtest4j.drivers</groupId>
    <artifactId>loadtest4j-wrk</artifactId>
    <scope>test</scope>
</dependency>   
```

### 2. Create the load tester

Use **either** the Factory **or** the Builder.

#### Factory

```java
LoadTester loadTester = LoadTesterFactory.getLoadTester();
```

```properties
# src/test/resources/loadtest4j.properties

loadtest4j.driver.connections = 1
loadtest4j.driver.duration = 30
loadtest4j.driver.threads = 1
loadtest4j.driver.url = https://example.com
```

#### Builder

```java
LoadTester loadTester = WrkBuilder.withUrl("https://example.com")
                                  .withConnections(1)
                                  .withDuration(Duration.ofSeconds(30))
                                  .withThreads(1)
                                  .build();
```

### 3. Write load tests

Write load tests with your favorite language, test framework, and assertions. See the [loadtest4j documentation](https://www.loadtest4j.org) for further instructions.

```java
public class PetStoreLT {

    private static final LoadTester loadTester = /* see step 2 */ ;

    @Test
    public void shouldFindPets() {
        List<Request> requests = List.of(Request.get("/pet/findByStatus")
                                                .withHeader("Accept", "application/json")
                                                .withQueryParam("status", "available"));

        Result result = loadTester.run(requests);

        assertThat(result.getResponseTime().getPercentile(90))
            .isLessThanOrEqualTo(Duration.ofMillis(500));
    }
}
```

## Limitations

- The percentile distribution is accurate to 3 decimal places. For example, it can resolve a difference between the 99.998th percentile and the 99.999th percentile, but not a smaller step size.
- Wrk's design means it has to read files into memory for file uploads. Wrk will therefore break if you give it a large file.