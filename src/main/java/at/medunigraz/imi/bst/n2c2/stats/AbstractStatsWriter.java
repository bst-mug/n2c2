package at.medunigraz.imi.bst.n2c2.stats;

import java.io.OutputStream;

public abstract class AbstractStatsWriter implements StatsWriter {

    protected OutputStream output;

    protected static final String GROUPED_BY = "Criterion";
    protected static final String[] METRICS = new String[]{"Prec_met", "Rec_met", "Speci_met", "F1_met", "Prec_notmet", "Rec_notmet", "F1_notmet", "F1_overall", "AUC_overall"};

    public AbstractStatsWriter(OutputStream output) {
        this.output = output;
    }

    protected abstract void writeHeader();
}
