package at.medunigraz.imi.bst.n2c2.stats;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.metrics.MetricSet;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class XMLStatsWriterTest {
    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder();

    private static String normalizeLineDelimiter(String s) {
        return s.replaceAll("[\r\n]+", "");
    }

    @Test
    public void write() throws IOException {
        final File actualFile = testFolder.newFile("test.xml");
        final File expectedFile = new File(getClass().getResource("/stats/expected.xml").getFile());

        StatsWriter writer = new XMLStatsWriter(actualFile);

        MetricSet metrics = new MetricSet();
        metrics.withPrecision(Criterion.ABDOMINAL, Eligibility.MET, 0.6167);
        metrics.withRecall(Criterion.ABDOMINAL, Eligibility.NOT_MET, 0.7459);
        writer.write(metrics);
        writer.close();

        String expected = XMLStatsWriterTest.normalizeLineDelimiter(FileUtils.readFileToString(expectedFile, "UTF-8"));
        String actual = XMLStatsWriterTest.normalizeLineDelimiter(FileUtils.readFileToString(actualFile, "UTF-8"));

        assertEquals(expected, actual);
    }

}