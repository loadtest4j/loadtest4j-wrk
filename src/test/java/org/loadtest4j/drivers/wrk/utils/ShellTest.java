package org.loadtest4j.drivers.wrk.utils;

import org.loadtest4j.drivers.wrk.junit.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@Category(IntegrationTest.class)
public class ShellTest {
    @Test
    public void testWaitFor() {
        final Shell sut = new Shell();

        final Command command = new Command(Collections.emptyList(), Collections.emptyMap(), "whoami");
        final int exitStatus = sut.start(command).waitFor();

        assertThat(exitStatus).isEqualTo(0);
    }
}
