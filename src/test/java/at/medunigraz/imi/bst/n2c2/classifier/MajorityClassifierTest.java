package at.medunigraz.imi.bst.n2c2.classifier;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MajorityClassifierTest {

    @Test
    public void trainPredict() {
        List<Patient> train = new ArrayList<>();

        // 2/3 not met is the expected output
        train.add(new Patient().withCriterion(Criterion.ABDOMINAL, Eligibility.MET));
        train.add(new Patient().withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET));
        train.add(new Patient().withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET));

        MajorityClassifier mc = new MajorityClassifier(Criterion.ABDOMINAL);
        mc.train(train);

        assertEquals(Eligibility.NOT_MET, mc.predict(new Patient()));
    }
}