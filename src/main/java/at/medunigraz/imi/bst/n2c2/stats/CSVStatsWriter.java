package at.medunigraz.imi.bst.n2c2.stats;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
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
        writer.writeNext(new String[]{GROUPED_BY, METRIC_NAME});
        try {
            flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void write(Criterion c, Double accuracy) {
        String[] entries = new String[]{c.name(), String.valueOf(accuracy)};
        writer.writeNext(entries);
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
