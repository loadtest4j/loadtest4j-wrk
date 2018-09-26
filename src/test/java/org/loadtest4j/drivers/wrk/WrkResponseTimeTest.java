package org.loadtest4j.drivers.wrk;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.drivers.wrk.junit.UnitTest;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@Category(UnitTest.class)
public class WrkResponseTimeTest {

    @Test
    public void testGetPercentile() {
        final DriverResponseTime responseTime = new WrkResponseTime(mapOf(bd("50"), 1000L));

        assertThat(responseTime.getPercentile(50)).isEqualTo(Duration.ofMillis(1));
    }

    @Test
    public void testGetDecimalPercentile() {
        final WrkResponseTime responseTime = new WrkResponseTime(mapOf(bd("50.5"), 1000L));

        assertThat(responseTime.getDoublePercentile(50.5)).isEqualTo(Duration.ofMillis(1));
    }

    @Test
    public void testGetDecimalPercentileAt3DecimalPlaces() {
        final WrkResponseTime responseTime = new WrkResponseTime(mapOf(bd("50.501"), 1000L));

        assertThat(responseTime.getDoublePercentile(50.501)).isEqualTo(Duration.ofMillis(1));
    }

    @Test
    public void testGetDecimalPercentileWithTrailingZeroes() {
        final WrkResponseTime responseTime = new WrkResponseTime(mapOf(bd("50.5"), 1000L, bd("50.501"), 3000L));

        assertThat(responseTime.getDoublePercentile(50.500)).isEqualTo(Duration.ofMillis(1));
    }

    @Test
    public void testGetDecimalPercentileBeyond3DecimalPlacesFails() {
        final WrkResponseTime responseTime = new WrkResponseTime(mapOf(bd("50.000"), 1000L, bd("50.001"), 3000L));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> responseTime.getDoublePercentile(50.000005))
                .withMessage("The Wrk driver only supports percentile queries up to 3 decimal places.");
    }

    @Test
    public void testGetMissingDecimalPercentileFails() {
        final WrkResponseTime responseTime = new WrkResponseTime(mapOf(bd("50.4"), 1000L, bd("50.6"), 3000L));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> responseTime.getDoublePercentile(50.401))
                .withMessage("The Wrk driver could not find a response time value for that percentile.");
    }

    @Test
    public void testGetMaxPercentile() {
        final DriverResponseTime responseTime = new WrkResponseTime(mapOf(bd("100"), 1000L));

        assertThat(responseTime.getPercentile(100)).isEqualTo(Duration.ofMillis(1));
    }

    @Test
    public void testGetMinPercentile() {
        final DriverResponseTime responseTime = new WrkResponseTime(mapOf(bd("0"), 1000L));

        assertThat(responseTime.getPercentile(0)).isEqualTo(Duration.ofMillis(1));
    }

    @Test
    public void testGetTooLowPercentileFails() {
        final DriverResponseTime responseTime = new WrkResponseTime(mapOf(bd("-1"), 1000L));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> responseTime.getPercentile(-1))
                .withMessage("A percentile must be between 0 and 100");
    }

    @Test
    public void testGetTooHighPercentileFails() {
        final DriverResponseTime responseTime = new WrkResponseTime(mapOf(bd("101"), 1000L));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> responseTime.getPercentile(101))
                .withMessage("A percentile must be between 0 and 100");
    }

    @Test
    public void testGetMissingPercentileFails() {
        final DriverResponseTime responseTime = new WrkResponseTime(emptyMap());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> responseTime.getPercentile(50))
                .withMessage("The Wrk driver could not find a response time value for that percentile.");
    }

    private static BigDecimal bd(String d) {
        return new BigDecimal(d);
    }

    private static <K, V> TreeMap<K, V> emptyMap() {
        return new TreeMap<>();
    }

    private static <K, V> TreeMap<K, V> mapOf(K k1, V v1) {
        final TreeMap<K, V> m = new TreeMap<>();
        m.put(k1, v1);
        return m;
    }

    private static <K, V> TreeMap<K, V> mapOf(K k1, V v1, K k2, V v2) {
        final TreeMap<K, V> m = new TreeMap<>();
        m.put(k1, v1);
        m.put(k2, v2);
        return m;
    }
}
