package at.medunigraz.imi.bst.n2c2.model.metrics;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;

import java.util.HashMap;
import java.util.Map;

public class OfficialMetrics implements Metrics {

    private Map<Criterion, Map<Eligibility, Metrics>> metrics = new HashMap<>();

    public OfficialMetrics() {
        for (Criterion c : Criterion.values()) {
            Map<Eligibility, Metrics> mapPerCriterion = new HashMap<>();
            for (Eligibility e : Eligibility.values()) {
                mapPerCriterion.put(e, new Metrics());
            }
            metrics.put(c, mapPerCriterion);
        }
    }

    public OfficialMetrics withPrecision(Criterion criterion, Eligibility eligibility, double value) {
        metrics.get(criterion).get(eligibility).precision = value;
        return this;
    }

    public OfficialMetrics withRecall(Criterion criterion, Eligibility eligibility, double value) {
        metrics.get(criterion).get(eligibility).recall = value;
        return this;
    }

    public OfficialMetrics withSpecificity(Criterion criterion, Eligibility eligibility, double value) {
        metrics.get(criterion).get(eligibility).specificity = value;
        return this;
    }

    public OfficialMetrics withF1(Criterion criterion, Eligibility eligibility, double value) {
        metrics.get(criterion).get(eligibility).f1 = value;
        return this;
    }

    public OfficialMetrics withAreaUnderCurve(Criterion criterion, Eligibility eligibility, double value) {
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
