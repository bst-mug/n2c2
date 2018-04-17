package at.medunigraz.imi.bst.n2c2.stats;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.metrics.OfficialMetrics;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.Charset;

public class CSVStatsWriter extends AbstractStatsWriter {
    private CSVWriter writer;

    public CSVStatsWriter(OutputStream output) {
        super(output);
        writer = new CSVWriter(new OutputStreamWriter(output, Charset.forName("UTF-8")));
        writeHeader();
    }

    public CSVStatsWriter(File outputFile) throws IOException {
        this(new FileOutputStream(outputFile));
    }

    protected void writeHeader() {
        // Grouped by + metrics
        String[] header = new String[1 + METRICS.length];
        header[0] = GROUPED_BY;

        // Copy metrics name to the header
        System.arraycopy(METRICS, 0, header, 1, METRICS.length);

        writer.writeNext(header);
        try {
            flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    @Deprecated
    public void write(Criterion c, Double accuracy) {
        String[] entries = new String[]{c.name(), String.valueOf(accuracy)};
        writer.writeNext(entries);
    }

    public void write(OfficialMetrics metrics) {
        for (Criterion c : Criterion.values()) {
            double[] values = metrics.getMetricsArray(c);

            // Criterion + values
            String[] entries = new String[1 + values.length];

            entries[0] = c.name();
            for (int i = 0; i < values.length; i++) {
                entries[i + 1] = String.valueOf(values[i]);
            }

            writer.writeNext(entries);
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    @Override
    public void flush() throws IOException {
        writer.flush();
    }
}
