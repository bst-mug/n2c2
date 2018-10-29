package at.medunigraz.imi.bst.n2c2.classifier.rules;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.rules.RuleBasedClassifier;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class RuleBasedClassifierTest {

    @Test
    public void predictSample() throws IOException, SAXException {
        final File SAMPLE = new File(getClass().getResource("/gold-standard/sample.xml").getPath());
        Patient p = new PatientDAO().fromXML(SAMPLE);

        assertEquals(Eligibility.NOT_MET, (new RuleBasedClassifier(Criterion.ABDOMINAL).predict(p)));
        assertEquals(Eligibility.MET, (new RuleBasedClassifier(Criterion.ADVANCED_CAD).predict(p)));
        assertEquals(Eligibility.NOT_MET, (new RuleBasedClassifier(Criterion.ALCOHOL_ABUSE).predict(p)));
        assertEquals(Eligibility.MET, (new RuleBasedClassifier(Criterion.ASP_FOR_MI).predict(p)));
        assertEquals(Eligibility.NOT_MET, (new RuleBasedClassifier(Criterion.CREATININE).predict(p)));
        assertEquals(Eligibility.MET, (new RuleBasedClassifier(Criterion.DIETSUPP_2MOS).predict(p)));
        assertEquals(Eligibility.NOT_MET, (new RuleBasedClassifier(Criterion.DRUG_ABUSE).predict(p)));
        assertEquals(Eligibility.MET, (new RuleBasedClassifier(Criterion.ENGLISH).predict(p)));
//        assertEquals(Eligibility.MET, (new RuleBasedClassifier(Criterion.HBA1C).predict(p)));
        assertEquals(Eligibility.NOT_MET, (new RuleBasedClassifier(Criterion.KETO_1YR).predict(p)));
        assertEquals(Eligibility.MET, (new RuleBasedClassifier(Criterion.MAJOR_DIABETES).predict(p)));
        assertEquals(Eligibility.MET, (new RuleBasedClassifier(Criterion.MAKES_DECISIONS).predict(p)));
//        assertEquals(Eligibility.MET, (new RuleBasedClassifier(Criterion.MI_6MOS).predict(p)));
    }
}
