package org.loadtest4j.drivers.wrk.script;

import org.junit.Test;

public abstract class BodyMatcherTest {
    @Test
    public abstract void shouldMatchOnString();

    @Test
    public abstract void shouldMatchOnStringPart();

    @Test
    public abstract void shouldMatchOnFilePart();
}
