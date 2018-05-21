# loadtest4j-wrk

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
    <version>[a git tag]</version>
</dependency>
```

Then install the `wrk` command line executable, and make it available on your `$PATH`. If you have Homebrew then just do
 `brew install wrk`.

## Usage

Add the file `loadtest4j.properties` to your `src/test/resources` directory and configure the load test driver:

```
loadtest4j.driver = com.github.loadtest4j.drivers.wrk.WrkFactory
loadtest4j.driver.duration = 30
loadtest4j.driver.url = https://example.com
```

Then write your load tests in Java using the standard `LoadTester` API.