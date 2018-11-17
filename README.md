# loadtest4j-wrk

[![Build Status](https://travis-ci.com/loadtest4j/loadtest4j-wrk.svg?branch=master)](https://travis-ci.com/loadtest4j/loadtest4j-wrk)
[![Codecov](https://codecov.io/gh/loadtest4j/loadtest4j-wrk/branch/master/graph/badge.svg)](https://codecov.io/gh/loadtest4j/loadtest4j-wrk)
[![Maven Central](https://img.shields.io/maven-central/v/org.loadtest4j.drivers/loadtest4j-wrk.svg)](http://repo2.maven.org/maven2/org/loadtest4j/drivers/loadtest4j-wrk/)

Wrk driver for loadtest4j.

## Setup

1. **Add the library** to your `pom.xml`:

    ```xml
    <dependency>
        <groupId>org.loadtest4j.drivers</groupId>
        <artifactId>loadtest4j-wrk</artifactId>
        <scope>test</scope>
    </dependency>   
    ```

2. **Configure the driver** in `src/test/resources/loadtest4j.properties`:
    
    ```properties
    loadtest4j.driver.connections = 1
    loadtest4j.driver.duration = 30
    loadtest4j.driver.threads = 1
    loadtest4j.driver.url = https://example.com
    ```

3. **Install the `wrk` executable** on your `$PATH`:

    - Homebrew: run `brew install wrk`.
    - Compile-from-source: check out the instructions in [`.travis.yml`](.travis.yml).

4. **Write your load tests** using the standard [LoadTester API](https://github.com/loadtest4j/loadtest4j).

## Limitations

- The percentile distribution is accurate to 3 decimal places. For example, it can resolve a difference between the 99.998th percentile and the 99.999th percentile, but not a smaller step size.
- Wrk's design means it has to read files into memory for file uploads. Wrk will therefore break if you give it a large file.