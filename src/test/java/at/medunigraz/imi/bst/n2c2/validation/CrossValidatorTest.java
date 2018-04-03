package at.medunigraz.imi.bst.n2c2.validation;

import at.medunigraz.imi.bst.n2c2.classifier.factory.BaselineClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.evaluator.InterAnnotatorAgreement;
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
        Assume.assumeTrue(InterAnnotatorAgreement.scriptExists());
    }

    @Test
    public void evaluate() throws FileNotFoundException {
        ClassifierFactory factory = new BaselineClassifierFactory();
        Evaluator evaluator = new InterAnnotatorAgreement();

        // 3/5 not met is the expected output
        List<Patient> patients = new ArrayList<>();
        patients.add(new Patient().withID("1.xml").withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET));
        patients.add(new Patient().withID("2.xml").withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET));
        patients.add(new Patient().withID("3.xml").withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET));
        patients.add(new Patient().withID("4.xml").withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET));
        patients.add(new Patient().withID("5.xml").withCriterion(Criterion.ABDOMINAL, Eligibility.MET));


        CrossValidator cv = new CrossValidator(patients, factory, evaluator);
        Map<Criterion, Double> metrics = cv.evaluate(patients.size());

        assertEquals(0.8, metrics.get(Criterion.ABDOMINAL), 0.00001);
    }
}