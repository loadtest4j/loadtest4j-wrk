package com.github.loadtest4j.drivers.wrk.shell;

import com.github.loadtest4j.drivers.wrk.junit.UnitTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class ArgumentBuilderTest {
    @Test
    public void testAddArgument() {
        final ArgumentBuilder sut = new ArgumentBuilder();

        final List<String> args = sut.addArgument("foo").build();

        assertThat(args).containsExactly("foo");
    }

    @Test
    public void testAddNamedArgument() {
        final ArgumentBuilder sut = new ArgumentBuilder();

        final List<String> args = sut.addNamedArgument("--foo", "bar").build();

        assertThat(args).containsExactly("--foo", "bar");
    }
}
