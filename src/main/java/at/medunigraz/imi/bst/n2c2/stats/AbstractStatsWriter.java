package at.medunigraz.imi.bst.n2c2.stats;

import java.io.OutputStream;

public abstract class AbstractStatsWriter implements StatsWriter {

    protected OutputStream output;

    protected static final String GROUPED_BY = "Criterion";

    public AbstractStatsWriter(OutputStream output) {
        this.output = output;
    }
}
