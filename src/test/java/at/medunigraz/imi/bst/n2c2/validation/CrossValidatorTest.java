package at.medunigraz.imi.bst.n2c2.validation;

import at.medunigraz.imi.bst.n2c2.classifier.factory.BaselineClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.evaluator.OfficialEvaluator;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CrossValidatorTest {

    @Before
    public void SetUp() {
        Assume.assumeTrue(OfficialEvaluator.scriptExists());
    }

    @Test
    public void evaluate() throws FileNotFoundException {
        ClassifierFactory factory = new BaselineClassifierFactory();
        Evaluator evaluator = new OfficialEvaluator();

        // 4/5 not met is the expected output
        List<Patient> patients = emptyPatients(5);
        patients.get(0).withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET);
        patients.get(1).withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET);
        patients.get(2).withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET);
        patients.get(3).withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET);
        patients.get(4).withCriterion(Criterion.ABDOMINAL, Eligibility.MET);

        CrossValidator cv = new CrossValidator(patients, factory, evaluator);
        Map<Criterion, Double> metrics = cv.evaluate(patients.size());

        // Official evaluation script average per class (2), so expected is 4/5/2.
        assertEquals(0.4, metrics.get(Criterion.ABDOMINAL), 0.00001);
    }

    private List<Patient> emptyPatients(int n) {
        List<Patient> patients = new ArrayList<>(n);

        // The official evaluation script does not like XMLs without all tags
        for (int i = 0; i < n; i++) {
            // XML Transformer doesn't like empty text in some Java versions
            Patient p = new Patient().withID(String.format("%d.xml", i)).withText("abc");
            for (Criterion c : Criterion.classifiableValues()) {
                p.withCriterion(c, Eligibility.NOT_MET);
            }
            patients.add(p);
        }

        return patients;
    }
}