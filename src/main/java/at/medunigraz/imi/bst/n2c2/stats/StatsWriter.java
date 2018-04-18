package at.medunigraz.imi.bst.n2c2.stats;

import at.medunigraz.imi.bst.n2c2.model.metrics.MetricSet;

import java.io.Closeable;
import java.io.Flushable;

public interface StatsWriter extends Closeable, Flushable {

    void write(MetricSet metrics);
}
