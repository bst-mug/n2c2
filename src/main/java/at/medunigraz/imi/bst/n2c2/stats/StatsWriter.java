package at.medunigraz.imi.bst.n2c2.stats;

import at.medunigraz.imi.bst.n2c2.model.Criterion;

import java.io.Closeable;
import java.io.Flushable;
import java.util.Map;

public interface StatsWriter extends Closeable, Flushable {

    void write(Criterion c, Double accuracy);

    default void write(Map<Criterion, Double> accuracyByCriterion) {
        accuracyByCriterion.forEach((key, value) -> write(key, value));
    }
}
