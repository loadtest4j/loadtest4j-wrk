package com.github.loadtest4j.drivers.wrk.utils;

import com.github.loadtest4j.drivers.wrk.junit.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.assertj.core.api.Assertions.assertThat;

@Category(IntegrationTest.class)
public class AutoDeletingTempFileTest {
    @Test
    public void testCreateAndClose() {
        final AutoDeletingTempFile sut = AutoDeletingTempFile.create("foo");

        assertThat(sut.exists()).isTrue();

        sut.close();

        assertThat(sut.exists()).isFalse();
    }
}
