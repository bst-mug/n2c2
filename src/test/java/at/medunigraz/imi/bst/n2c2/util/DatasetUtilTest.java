package at.medunigraz.imi.bst.n2c2.util;

import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class DatasetUtilTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void saveToFolder() throws IOException {
        final String filename = "test.xml";

        List<Patient> patientList = new ArrayList<>();
        patientList.add(new Patient().withID(filename).withText("abc"));

        File output = testFolder.newFolder();

        DatasetUtil.saveToFolder(patientList, output);

        File expected = new File(output, filename);
        assertTrue(expected.exists());
    }
}