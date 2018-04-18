package at.medunigraz.imi.bst.n2c2.stats;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.metrics.MetricSet;

import java.io.Closeable;
import java.io.Flushable;
import java.util.Arrays;
import java.util.Map;

public interface StatsWriter extends Closeable, Flushable {

    @Deprecated
    void write(Criterion c, Double accuracy);

    @Deprecated
    default void write(Map<Criterion, Double> accuracyByCriterion) {
        accuracyByCriterion.forEach((key, value) -> write(key, value));
    }

    default void write(MetricSet metrics) {
        Arrays.stream(Criterion.classifiableValues()).forEach(c -> write(c, metrics.getOfficialRankingMeasureByCriterion(c)));
    }
}
