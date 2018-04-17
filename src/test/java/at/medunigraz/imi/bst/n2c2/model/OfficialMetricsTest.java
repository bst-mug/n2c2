package at.medunigraz.imi.bst.n2c2.model;

import at.medunigraz.imi.bst.n2c2.model.metrics.OfficialMetrics;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OfficialMetricsTest {

    @Test
    public void getPrecision() {
        OfficialMetrics metrics = new OfficialMetrics();

        final double value = 0.6167;

        metrics.withPrecision(Criterion.ABDOMINAL, Eligibility.MET, value);
        double actual = metrics.getPrecision(Criterion.ABDOMINAL, Eligibility.MET);

        assertEquals(value, actual, 0.00001);
    }
}