package at.medunigraz.imi.bst.n2c2.model;

import at.medunigraz.imi.bst.n2c2.model.metrics.MetricSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MetricSetTest {

    private static final double DELTA = 0.00001;

    @Test
    public void getPrecision() {
        MetricSet metrics = new MetricSet();

        final double value = 0.6167;

        metrics.withPrecision(Criterion.ABDOMINAL, Eligibility.MET, value);
        double actual = metrics.getPrecision(Criterion.ABDOMINAL, Eligibility.MET);

        assertEquals(value, actual, DELTA);
    }

    @Test
    public void add() {
        MetricSet metrics = new MetricSet();
        metrics.withPrecision(Criterion.ABDOMINAL, Eligibility.MET, 0.6167);
        metrics.withRecall(Criterion.ADVANCED_CAD, Eligibility.NOT_MET, 0.2124);

        MetricSet addend = new MetricSet();
        addend.withPrecision(Criterion.ABDOMINAL, Eligibility.MET, 0.5234);
        addend.withRecall(Criterion.ADVANCED_CAD, Eligibility.NOT_MET, 0.2522);

        metrics.add(addend);

        assertEquals(0.6167 + 0.5234, metrics.getPrecision(Criterion.ABDOMINAL, Eligibility.MET), DELTA);
        assertEquals(0.2124 + 0.2522, metrics.getRecall(Criterion.ADVANCED_CAD, Eligibility.NOT_MET), DELTA);

        // Sanity check
        assertEquals(0, metrics.getF1(Criterion.ASP_FOR_MI, Eligibility.MET), DELTA);
    }

    @Test
    public void average() {
        MetricSet metrics = new MetricSet();
        metrics.withPrecision(Criterion.ABDOMINAL, Eligibility.MET, 0.6167);
        metrics.withRecall(Criterion.ADVANCED_CAD, Eligibility.NOT_MET, 0.2124);

        metrics.divideBy(10);

        assertEquals(0.6167 / 10, metrics.getPrecision(Criterion.ABDOMINAL, Eligibility.MET), DELTA);
        assertEquals(0.2124 / 10, metrics.getRecall(Criterion.ADVANCED_CAD, Eligibility.NOT_MET), DELTA);

        // Sanity check
        assertEquals(0, metrics.getF1(Criterion.ASP_FOR_MI, Eligibility.MET), DELTA);
    }
}