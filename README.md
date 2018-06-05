# loadtest4j-wrk

[![Build Status](https://travis-ci.com/loadtest4j/loadtest4j-wrk.svg?branch=master)](https://travis-ci.com/loadtest4j/loadtest4j-wrk)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d460fbdecdc14a9aa3b8d58dd9be999f)](https://www.codacy.com/app/loadtest4j/loadtest4j-wrk)
[![JitPack Release](https://jitpack.io/v/com.github.loadtest4j/loadtest4j-wrk.svg)](https://jitpack.io/#com.github.loadtest4j/loadtest4j-wrk)

The `wrk` driver for loadtest4j.

## Setup

Add the [JitPack](https://jitpack.io) repository to your pom.xml:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Then add this library:

```xml
<dependency>
    <groupId>com.github.loadtest4j</groupId>
    <artifactId>loadtest4j-wrk</artifactId>
    <version>[git tag]</version>
</dependency>
```

Then install the `wrk` command line executable, and make it available on your `$PATH`:

- With Homebrew: run `brew install wrk`.
- Without Homebrew: check out the installation method in `.travis.yml`.

## Usage

Add the file `loadtest4j.properties` to your `src/test/resources` directory and configure the load test driver:

```
loadtest4j.driver = com.github.loadtest4j.drivers.wrk.WrkFactory
loadtest4j.driver.duration = 30
loadtest4j.driver.url = https://example.com
```

Then write your load tests in Java using the standard `LoadTester` API.
