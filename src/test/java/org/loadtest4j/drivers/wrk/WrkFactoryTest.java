package org.loadtest4j.drivers.wrk;

import com.github.loadtest4j.loadtest4j.driver.Driver;
import com.github.loadtest4j.loadtest4j.driver.DriverFactory;
import org.loadtest4j.drivers.wrk.junit.UnitTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class WrkFactoryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private DriverFactory sut() {
        return new WrkFactory();
    }

    @Test
    public void testGetMandatoryProperties() {
        final DriverFactory sut = sut();

        final Set<String> mandatoryProperties = sut.getMandatoryProperties();

        assertThat(mandatoryProperties).containsExactly("connections", "duration", "threads", "url");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetMandatoryPropertiesIsImmutable() {
        final DriverFactory sut = sut();

        sut.getMandatoryProperties().add("foobarbaz123");
    }

    @Test
    public void testCreate() {
        final DriverFactory sut = sut();

        final Map<String, String> properties = new HashMap<>();
        properties.put("connections", "1");
        properties.put("duration", "2");
        properties.put("threads", "1");
        properties.put("url", "https://example.com");

        final Driver driver = sut.create(properties);

        assertThat(driver).isInstanceOf(Wrk.class);
    }
}
