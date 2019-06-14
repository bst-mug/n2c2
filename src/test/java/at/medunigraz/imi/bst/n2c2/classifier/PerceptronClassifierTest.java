package at.medunigraz.imi.bst.n2c2.classifier;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PerceptronClassifierTest {

    private static List<Patient> train = new ArrayList<>();

    static {
        train.add(new Patient().withText("abdominal surgery").withCriterion(Criterion.ABDOMINAL, Eligibility.MET));
        train.add(new Patient().withText("head surgery").withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET));
    }

    @Test
    public void trainPredictSingle() {
        PerceptronClassifier mc = new PerceptronClassifier(Criterion.ABDOMINAL, true);
        mc.train(train);

        assertEquals(Eligibility.MET, mc.predict(new Patient().withText("abdominal surgery")));
        assertEquals(Eligibility.NOT_MET, mc.predict(new Patient().withText("head surgery")));
    }

}