package at.medunigraz.imi.bst.n2c2.stats;

import at.medunigraz.imi.bst.n2c2.model.metrics.Metrics;

import java.io.Closeable;
import java.io.Flushable;

public interface StatsWriter extends Closeable, Flushable {

    void write(Metrics metrics);
}
