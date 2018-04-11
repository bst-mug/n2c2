package at.medunigraz.imi.bst.n2c2.model;

import java.util.HashMap;
import java.util.Map;

public class MetricSet {

    private Map<Criterion, Map<Eligibility, Metrics>> metrics = new HashMap<>();

    public MetricSet() {
        for (Criterion c : Criterion.values()) {
            Map<Eligibility, Metrics> mapPerCriterion = new HashMap<>();
            for (Eligibility e : Eligibility.values()) {
                mapPerCriterion.put(e, new Metrics());
            }
            metrics.put(c, mapPerCriterion);
        }
    }

    public MetricSet withPrecision(Criterion criterion, Eligibility eligibility, double value) {
        metrics.get(criterion).get(eligibility).precision = value;
        return this;
    }

    public MetricSet withRecall(Criterion criterion, Eligibility eligibility, double value) {
        metrics.get(criterion).get(eligibility).recall = value;
        return this;
    }

    public MetricSet withSpecificity(Criterion criterion, Eligibility eligibility, double value) {
        metrics.get(criterion).get(eligibility).specificity = value;
        return this;
    }

    public MetricSet withF1(Criterion criterion, Eligibility eligibility, double value) {
        metrics.get(criterion).get(eligibility).f1 = value;
        return this;
    }

    public MetricSet withAreaUnderCurve(Criterion criterion, Eligibility eligibility, double value) {
        metrics.get(criterion).get(eligibility).auc = value;
        return this;
    }

    public double getPrecision(Criterion criterion, Eligibility eligibility) {
        return metrics.get(criterion).get(eligibility).precision;
    }

    public double getRecall(Criterion criterion, Eligibility eligibility) {
        return metrics.get(criterion).get(eligibility).recall;
    }

    public double getSpecificity(Criterion criterion, Eligibility eligibility) {
        return metrics.get(criterion).get(eligibility).specificity;
    }

    public double getAreaUnderCurve(Criterion criterion, Eligibility eligibility) {
        return metrics.get(criterion).get(eligibility).auc;
    }

    public double getF1(Criterion criterion, Eligibility eligibility) {
        return metrics.get(criterion).get(eligibility).f1;
    }

    public double getOfficialRankingMeasureByCriterion(Criterion criterion) {
        return getF1(criterion, Eligibility.OVERALL);
    }

    public double getOfficialRankingMeasure() {
        return getOfficialRankingMeasureByCriterion(Criterion.OVERALL_MICRO);
    }

    public class Metrics {
        public double precision = 0;
        public double recall = 0;
        public double specificity = 0;
        public double f1 = 0;
        public double auc = 0;
    }

}
