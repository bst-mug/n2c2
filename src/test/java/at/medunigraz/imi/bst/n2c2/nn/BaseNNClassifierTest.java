package at.medunigraz.imi.bst.n2c2.nn;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class BaseNNClassifierTest {

    protected static final File SAMPLE = new File(BaseNNClassifierTest.class.getResource("/gold-standard/sample.xml").getPath());

    protected BaseNNClassifier classifier;

    @Test
    public void predictSample() throws IOException, SAXException {
        Patient p = new PatientDAO().fromXML(SAMPLE);

        // For test purposes only, we train and test on the same single patient
        List<Patient> train = new ArrayList<>();
        train.add(p);

        classifier.deleteModelDir(train);	// Delete any previously trained models, to ensure training is tested
        classifier.train(train);

        assertSamplePatient(classifier, p);
    }

    protected void assertSamplePatient(BaseNNClassifier nn, Patient p) {
        assertEquals(Eligibility.NOT_MET, nn.predict(p, Criterion.ABDOMINAL));
        assertEquals(Eligibility.MET, nn.predict(p, Criterion.ADVANCED_CAD));
        assertEquals(Eligibility.NOT_MET, nn.predict(p, Criterion.ALCOHOL_ABUSE));
        assertEquals(Eligibility.MET, nn.predict(p, Criterion.ASP_FOR_MI));
        assertEquals(Eligibility.NOT_MET, nn.predict(p, Criterion.CREATININE));
        assertEquals(Eligibility.MET, nn.predict(p, Criterion.DIETSUPP_2MOS));
        assertEquals(Eligibility.NOT_MET, nn.predict(p, Criterion.DRUG_ABUSE));
        assertEquals(Eligibility.MET, nn.predict(p, Criterion.ENGLISH));
        assertEquals(Eligibility.MET, nn.predict(p, Criterion.HBA1C));
        assertEquals(Eligibility.NOT_MET, nn.predict(p, Criterion.KETO_1YR));
        assertEquals(Eligibility.MET, nn.predict(p, Criterion.MAJOR_DIABETES));
        assertEquals(Eligibility.MET, nn.predict(p, Criterion.MAKES_DECISIONS));
        assertEquals(Eligibility.MET, nn.predict(p, Criterion.MI_6MOS));
    }
}
