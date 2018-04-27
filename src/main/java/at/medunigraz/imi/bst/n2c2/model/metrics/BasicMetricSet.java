package at.medunigraz.imi.bst.n2c2.model.metrics;

import at.medunigraz.imi.bst.n2c2.model.Criterion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasicMetricSet implements Metrics {

    private static final String ACCURACY = "Accuracy";
    private static final String TP = "tp";
    private static final String FP = "fp";
    private static final String TN = "tn";
    private static final String FN = "fn";

    private Map<Criterion, BasicMetrics> metricsPerCriterion = new HashMap<>();

    public BasicMetricSet() {
        // TODO lazy initialization
        for (Criterion c : Criterion.values()) {
            metricsPerCriterion.put(c, new BasicMetrics(0));
        }
    }

    public List<String> getMetricNames() {
        List<String> ret = new ArrayList<>();

        ret.add(ACCURACY);

        ret.add(TP);
        ret.add(FP);
        ret.add(TN);
        ret.add(FN);

        return ret;
    }

    public BasicMetricSet withBasicMetrics(Criterion c, BasicMetrics metrics) {
        metricsPerCriterion.put(c, metrics);
        return this;
    }

    public Map<String, Double> getMetrics(Criterion c) {
        Map<String, Double> ret = new HashMap<>();

        ret.put(ACCURACY, metricsPerCriterion.get(c).getAccuracy());

        ret.put(TP, metricsPerCriterion.get(c).getTruePositives());
        ret.put(FP, metricsPerCriterion.get(c).getFalsePositives());
        ret.put(TN, metricsPerCriterion.get(c).getTrueNegatives());
        ret.put(FN, metricsPerCriterion.get(c).getFalseNegatives());

        return ret;
    }

    @Override
    public double getOfficialRankingMeasure() {
        return metricsPerCriterion.get(Criterion.OVERALL_MICRO).getAccuracy();
    }

    @Override
    public double getOfficialRankingMeasureByCriterion(Criterion c) {
        return metricsPerCriterion.get(c).getAccuracy();
    }

    @Override
    public void add(Metrics addend) {
        if (!(addend instanceof BasicMetricSet)) {
            throw new UnsupportedOperationException("Can only add metrics of the same type.");
        }

        add((BasicMetricSet) addend);
    }

    public void add(BasicMetricSet addend) {
        for (Criterion c : Criterion.values()) {
            metricsPerCriterion.get(c).add(addend.metricsPerCriterion.get(c));
        }

    }

    @Override
    public void divideBy(double divisor) {
        for (Criterion c : Criterion.values()) {
            metricsPerCriterion.get(c).divideBy(divisor);
        }
    }
}
