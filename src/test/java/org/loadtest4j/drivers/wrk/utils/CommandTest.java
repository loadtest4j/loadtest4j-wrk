package org.loadtest4j.drivers.wrk.utils;

import org.loadtest4j.drivers.wrk.junit.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class CommandTest {
    private final Command command = new Command(Arrays.asList("foo", "bar"), "whoami");

    @Test
    public void testGetLaunchPath() {
        assertThat(command.getLaunchPath()).isEqualTo("whoami");
    }

    @Test
    public void testGetArguments() {
        assertThat(command.getArguments()).containsExactly("foo", "bar");
    }
}
