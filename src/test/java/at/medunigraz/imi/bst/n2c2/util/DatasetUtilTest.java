package at.medunigraz.imi.bst.n2c2.util;

import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
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

    @Test
    public void slice() {
        List<Patient> patients = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            patients.add(new Patient().withID(String.valueOf(i)));
        }

        int[] indices = new int[]{0, 3, 4};
        List<Patient> slicedPatients = DatasetUtil.slice(patients, indices);
        assertEquals(indices.length, slicedPatients.size());
        assertTrue(slicedPatients.get(0).getID().equals("0"));
        assertTrue(slicedPatients.get(1).getID().equals("3"));
        assertTrue(slicedPatients.get(2).getID().equals("4"));
    }

    @Test
    public void sliceWithIndices() {
        List<Patient> patients = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            patients.add(new Patient().withID(String.valueOf(i)));
        }

        int[] indices = new int[]{0, 3, 4};

        List<Patient> slicedPatients = DatasetUtil.slice(patients, indices, 0, 3);
        assertEquals(3, slicedPatients.size());
        assertTrue(slicedPatients.get(0).getID().equals("0"));
        assertTrue(slicedPatients.get(1).getID().equals("3"));
        assertTrue(slicedPatients.get(2).getID().equals("4"));

        slicedPatients = DatasetUtil.slice(patients, indices, 0, 2);
        assertEquals(2, slicedPatients.size());
        assertTrue(slicedPatients.get(0).getID().equals("0"));
        assertTrue(slicedPatients.get(1).getID().equals("3"));

        slicedPatients = DatasetUtil.slice(patients, indices, 1, 1);
        assertEquals(1, slicedPatients.size());
        assertTrue(slicedPatients.get(0).getID().equals("3"));

        // Length longer than indices
        slicedPatients = DatasetUtil.slice(patients, indices, 2, 2);
        assertEquals(1, slicedPatients.size());
        assertTrue(slicedPatients.get(0).getID().equals("4"));
    }

    @Test
    public void getRandomIndices() {
        final int SIZE = 5;
        final int MAX = 5;
        int[] chosen = DatasetUtil.getRandomIndices(SIZE, MAX);

        assertEquals(SIZE, chosen.length);

        // Indices should not repeat.
        // We create and set and check the length is the same.
        // We later use the set to further tests.
        Set<Integer> chosenSet = new HashSet<>();
        for (int i : chosen) {
            chosenSet.add(chosen[i]);
        }
        assertEquals(SIZE, chosenSet.size());

        // Elements should be smaller than MAX
        for (int i : chosen) {
            assertTrue(chosen[i] < MAX);
        }
    }
}