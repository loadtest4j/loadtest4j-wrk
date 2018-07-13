# loadtest4j-wrk

[![Build Status](https://travis-ci.com/loadtest4j/loadtest4j-wrk.svg?branch=master)](https://travis-ci.com/loadtest4j/loadtest4j-wrk)
[![Codecov](https://codecov.io/gh/loadtest4j/loadtest4j-wrk/branch/master/graph/badge.svg)](https://codecov.io/gh/loadtest4j/loadtest4j-wrk)
[![JitPack Release](https://jitpack.io/v/com.github.loadtest4j/loadtest4j-wrk.svg)](https://jitpack.io/#com.github.loadtest4j/loadtest4j-wrk)

The `wrk` driver for loadtest4j.

## Setup

1. **Add the library** to your `pom.xml`:

    ```xml
     <dependency>
         <groupId>com.github.loadtest4j</groupId>
         <artifactId>loadtest4j-wrk</artifactId>
         <version>[version]</version>
     </dependency>   
     
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    ```

2. **Configure the driver** in `src/test/resources/loadtest4j.properties`:
    
    ```properties
    loadtest4j.driver = com.github.loadtest4j.drivers.wrk.WrkFactory
    loadtest4j.driver.connections = 1
    loadtest4j.driver.duration = 30
    loadtest4j.driver.threads = 1
    loadtest4j.driver.url = https://example.com
    ```

3. **Install the `wrk` executable**, and make it available on your `$PATH`:

    - Homebrew: run `brew install wrk`.
    - Compile from source: check out the instructions in `.travis.yml`.

4. **Write your load tests** using the standard [LoadTester API](https://github.com/loadtest4j/loadtest4j).




