package org.loadtest4j.drivers.wrk.utils;

import org.loadtest4j.drivers.wrk.junit.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class CommandTest {
    private final Command command = new Command(Arrays.asList("foo", "bar"), Collections.singletonMap("foo", "bar"), "whoami");

    @Test
    public void shouldHaveLaunchPath() {
        assertThat(command.getLaunchPath()).isEqualTo("whoami");
    }

    @Test
    public void shouldHaveArguments() {
        assertThat(command.getArguments()).containsExactly("foo", "bar");
    }

    @Test
    public void shouldHaveEnv() {
        assertThat(command.getEnv()).containsEntry("foo", "bar");
    }
}
