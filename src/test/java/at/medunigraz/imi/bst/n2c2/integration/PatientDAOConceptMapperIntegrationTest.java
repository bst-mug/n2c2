package at.medunigraz.imi.bst.n2c2.integration;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.preprocess.conceptmapper.MetaMapLiteFacade;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class PatientDAOConceptMapperIntegrationTest {

    private static final File SAMPLE = new File(PatientDAOConceptMapperIntegrationTest.class.getResource("/gold-standard/sample.xml").getPath());
    private static Patient patient;

    public PatientDAOConceptMapperIntegrationTest() throws IOException, SAXException {
        patient = new PatientDAO().fromXML(SAMPLE);
    }

    @Before
    public void setUp() {
        Assume.assumeTrue(MetaMapLiteFacade.isModelsDirValid());
    }

    @Test
    public void getCUIs() {
        List<String> expected = new ArrayList<>();
        expected.add("C0043094"); // Weight Gain
        expected.add("C0013404"); // Dyspnea
        expected.add("C0020580"); // Hypesthesia

        List<String> actual = patient.getCUIs();
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void getUniqueCUIs() {
        Set<String> expected = new HashSet<>();
        expected.add("C0043094"); // Weight Gain
        expected.add("C0013404"); // Dyspnea
        expected.add("C0020580"); // Hypesthesia

        Set<String> actual = patient.getUniqueCUIs();
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void getAnnotatedText() {
        String expected = "<Patient|C0030705:Patients|> is concerned about <weight gain|C0043094:Weight Gain|>";

        String actual = patient.getAnnotatedText();
        assertTrue(actual.contains(expected));
    }

}
