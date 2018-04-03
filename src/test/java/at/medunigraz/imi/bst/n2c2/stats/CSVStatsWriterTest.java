package at.medunigraz.imi.bst.n2c2.stats;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class CSVStatsWriterTest {
    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void write() throws IOException {
        final File actualFile = testFolder.newFile("test.csv");
        final File expectedFile = new File(getClass().getResource("/stats/expected.csv").getFile());

        StatsWriter writer = new CSVStatsWriter(actualFile);
        writer.write(Criterion.ABDOMINAL, 0.5d);
        writer.close();

        String expected = FileUtils.readFileToString(expectedFile, "UTF-8").replaceAll("[\r\n]+", "");
        String actual = FileUtils.readFileToString(actualFile, "UTF-8").replaceAll("[\r\n]+", "");

        assertEquals(expected, actual);
    }
}