package at.medunigraz.imi.bst.n2c2.stats;

import java.io.OutputStream;

public abstract class AbstractStatsWriter implements StatsWriter {

    public AbstractStatsWriter(OutputStream output) {
    }

    protected abstract void writeHeader();
}
