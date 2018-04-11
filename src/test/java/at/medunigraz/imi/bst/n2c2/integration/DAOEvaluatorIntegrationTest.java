package at.medunigraz.imi.bst.n2c2.integration;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.evaluator.InterAnnotatorAgreement;
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
        Assume.assumeTrue(InterAnnotatorAgreement.scriptExists());
    }

    @Test
    public void regeneratedSampleFile() throws IOException, SAXException {
        final File equalResultsFolder = testFolder.newFolder();
        final File regeneratedSampleFile = new File(equalResultsFolder, "sample.xml");

        // <ABDOMINAL met="not met" />

        Patient patient = PATIENT_DAO.fromXML(sampleFile);
        PATIENT_DAO.toXML(patient, regeneratedSampleFile);

        InterAnnotatorAgreement iaa = new InterAnnotatorAgreement(goldStandardFolder, equalResultsFolder);
        assertEquals(1, iaa.getAccuracy(), 0.00001);
    }

    @Test
    public void changedSampleFile() throws IOException, SAXException {
        final File differentResultsFolder = testFolder.newFolder();
        final File differentSampleFile = new File(differentResultsFolder, "sample.xml");

        Patient patient = PATIENT_DAO.fromXML(sampleFile);
        patient.withCriterion(Criterion.ABDOMINAL, Eligibility.MET);
        PATIENT_DAO.toXML(patient, differentSampleFile);

        InterAnnotatorAgreement iaa = new InterAnnotatorAgreement(goldStandardFolder, differentResultsFolder);
        assertEquals(0, iaa.getAccuracy(), 0.00001);

        // TODO change after second version of iaa.py is released
        // assertEquals(0.9230769, iaa.getAccuracy(), 0.00001);  // 1 - 1/13 = 0.9230769
    }
}
