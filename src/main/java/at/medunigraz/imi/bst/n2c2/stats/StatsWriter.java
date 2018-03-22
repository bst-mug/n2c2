package at.medunigraz.imi.bst.n2c2.stats;

import at.medunigraz.imi.bst.n2c2.model.Criterion;

import java.io.Closeable;
import java.io.Flushable;
import java.util.Map;

public interface StatsWriter extends Closeable, Flushable {

    void write(Criterion c, Float accuracy);

    default void write(Map<Criterion, Float> accuracyByCriterion) {
        accuracyByCriterion.forEach((key, value) -> write(key, value));
    }
}
