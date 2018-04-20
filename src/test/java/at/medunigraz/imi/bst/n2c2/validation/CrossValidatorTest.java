package at.medunigraz.imi.bst.n2c2.validation;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.MajorityClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.evaluator.OfficialEvaluator;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.metrics.Metrics;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CrossValidatorTest {

    @Before
    public void SetUp() {
        Assume.assumeTrue(OfficialEvaluator.scriptExists());
    }

    @Test
    public void evaluate() throws FileNotFoundException {
        ClassifierFactory factory = new MajorityClassifierFactory();
        Evaluator evaluator = new OfficialEvaluator();

        // 4/5 not met is the expected output
        List<Patient> patients = DatasetUtil.generateEmptyPatients(5);
        patients.get(0).withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET);
        patients.get(1).withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET);
        patients.get(2).withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET);
        patients.get(3).withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET);
        patients.get(4).withCriterion(Criterion.ABDOMINAL, Eligibility.MET);

        CrossValidator cv = new CrossValidator(patients, factory, evaluator);
        Metrics metrics = cv.validate(patients.size());

        // Official evaluation script average per class (2), so expected is 4/5/2.
        assertEquals(0.4, metrics.getOfficialRankingMeasureByCriterion(Criterion.ABDOMINAL), 0.00001);
    }


}