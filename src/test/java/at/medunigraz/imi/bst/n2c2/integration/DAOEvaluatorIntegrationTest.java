package at.medunigraz.imi.bst.n2c2.integration;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.evaluator.OfficialEvaluator;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class DAOEvaluatorIntegrationTest {

    private static final String GOLD = "/gold-standard/";

    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder();

    private final File goldStandardFolder = new File(getClass().getResource(GOLD).getFile());
    private final File sampleFile = new File(goldStandardFolder, "sample.xml");

    private static final PatientDAO PATIENT_DAO = new PatientDAO();

    @Before
    public void SetUp() {
        Assume.assumeTrue(OfficialEvaluator.scriptExists());
    }

    @Test
    public void regeneratedSampleFile() throws IOException, SAXException {
        final File equalResultsFolder = testFolder.newFolder();
        final File regeneratedSampleFile = new File(equalResultsFolder, "sample.xml");

        // <ABDOMINAL met="not met" />

        Patient patient = PATIENT_DAO.fromXML(sampleFile);
        PATIENT_DAO.toXML(patient, regeneratedSampleFile);

        OfficialEvaluator iaa = new OfficialEvaluator(goldStandardFolder, equalResultsFolder);
        assertEquals(1, iaa.getOfficialRankingMeasure(), 0.00001);
    }

    @Test
    public void changedSampleFile() throws IOException, SAXException {
        final File differentResultsFolder = testFolder.newFolder();
        final File differentSampleFile = new File(differentResultsFolder, "sample.xml");

        Patient patient = PATIENT_DAO.fromXML(sampleFile);
        patient.withCriterion(Criterion.ABDOMINAL, Eligibility.MET);
        PATIENT_DAO.toXML(patient, differentSampleFile);

        OfficialEvaluator iaa = new OfficialEvaluator(goldStandardFolder, differentResultsFolder);
        assertEquals(0.915, iaa.getOfficialRankingMeasure(), 0.00001);
    }
}
