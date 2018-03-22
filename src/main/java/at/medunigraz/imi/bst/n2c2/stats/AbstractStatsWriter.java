package at.medunigraz.imi.bst.n2c2.stats;

import java.io.OutputStream;

public abstract class AbstractStatsWriter implements StatsWriter {

    protected OutputStream output;

    protected static final String GROUPED_BY = "Criterion";
    protected static final String METRIC_NAME = "Accuracy";

    public AbstractStatsWriter(OutputStream output) {
        this.output = output;
    }

    protected abstract void writeHeader();
}
