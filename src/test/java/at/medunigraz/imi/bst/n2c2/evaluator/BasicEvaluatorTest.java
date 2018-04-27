package at.medunigraz.imi.bst.n2c2.evaluator;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.metrics.BasicMetricSet;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BasicEvaluatorTest {

    @Test
    public void evaluate() {
        BasicEvaluator evaluator = new BasicEvaluator();

        List<Patient> gold = new ArrayList<>();
        gold.add(new Patient().withID("a").withCriterion(Criterion.ABDOMINAL, Eligibility.MET));
        gold.add(new Patient().withID("b").withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET));
        gold.add(new Patient().withID("c").withCriterion(Criterion.ABDOMINAL, Eligibility.MET));
        gold.add(new Patient().withID("d").withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET));

        List<Patient> results = new ArrayList<>();
        results.add(new Patient().withID("a").withCriterion(Criterion.ABDOMINAL, Eligibility.MET));
        results.add(new Patient().withID("b").withCriterion(Criterion.ABDOMINAL, Eligibility.MET));
        results.add(new Patient().withID("c").withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET));
        results.add(new Patient().withID("d").withCriterion(Criterion.ABDOMINAL, Eligibility.NOT_MET));

        evaluator.evaluate(gold, results);

        BasicMetricSet metricSet = (BasicMetricSet) evaluator.getMetrics();

        assertEquals(0.5, metricSet.getOfficialRankingMeasureByCriterion(Criterion.ABDOMINAL), 0.00001);

        // We do not macro-average over criteria
        assertEquals(0.5, metricSet.getOfficialRankingMeasureByCriterion(Criterion.OVERALL_MACRO), 0.00001);

        // Same as Criterion.ABDOMINAL
        assertEquals(0.5, metricSet.getOfficialRankingMeasureByCriterion(Criterion.OVERALL_MICRO), 0.00001);
    }
}