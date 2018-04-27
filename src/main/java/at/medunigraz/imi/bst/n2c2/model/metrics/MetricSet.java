package at.medunigraz.imi.bst.n2c2.model.metrics;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricSet implements Metrics {

    private static final String PRECISION_MET = "Prec_met";
    private static final String RECALL_MET = "Rec_met";
    private static final String SPECIFICITY_MET = "Speci_met";
    private static final String F1_MET = "F1_met";

    private static final String PRECISION_NOT_MET = "Prec_notmet";
    private static final String RECALL_NOT_MET = "Rec_notmet";
    private static final String F1_NOT_MET = "F1_notmet";

    private static final String F1_OVERALL = "F1_overall";
    private static final String AUC_OVERALL = "AUC_overall";

    private Map<Criterion, Map<Eligibility, OfficialMetrics>> metrics = new HashMap<>();

    public MetricSet() {
        // TODO lazy initialization
        for (Criterion c : Criterion.values()) {
            Map<Eligibility, OfficialMetrics> mapPerCriterion = new HashMap<>();
            for (Eligibility e : Eligibility.values()) {
                mapPerCriterion.put(e, new OfficialMetrics());
            }
            metrics.put(c, mapPerCriterion);
        }
    }

    public List<String> getMetricNames() {
        List<String> ret = new ArrayList<>();

        ret.add(PRECISION_MET);
        ret.add(RECALL_MET);
        ret.add(SPECIFICITY_MET);
        ret.add(F1_MET);

        ret.add(PRECISION_NOT_MET);
        ret.add(RECALL_NOT_MET);
        ret.add(F1_NOT_MET);

        ret.add(F1_OVERALL);
        ret.add(AUC_OVERALL);

        return ret;
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

    public Map<String, Double> getMetrics(Criterion c) {
        Map<String, Double> ret = new HashMap<>();

        ret.put(PRECISION_MET, getPrecision(c, Eligibility.MET));
        ret.put(RECALL_MET, getRecall(c, Eligibility.MET));
        ret.put(SPECIFICITY_MET, getSpecificity(c, Eligibility.MET));
        ret.put(F1_MET, getF1(c, Eligibility.MET));

        ret.put(PRECISION_NOT_MET, getPrecision(c, Eligibility.NOT_MET));
        ret.put(RECALL_NOT_MET, getRecall(c, Eligibility.NOT_MET));
        ret.put(F1_NOT_MET, getF1(c, Eligibility.NOT_MET));

        ret.put(F1_OVERALL, getF1(c, Eligibility.OVERALL));
        ret.put(AUC_OVERALL, getAreaUnderCurve(c, Eligibility.OVERALL));

        return ret;
    }

    @Override
    public void add(at.medunigraz.imi.bst.n2c2.model.metrics.Metrics addend) {
        if (!(addend instanceof MetricSet)) {
            throw new UnsupportedOperationException("Can only add metrics of the same type.");
        }

        add((MetricSet) addend);
    }

    /**
     * Adds another object to this one, used e.g. for cross-validation.
     *
     * @param addend
     * @return
     */
    public void add(MetricSet addend) {
        for (Criterion c : Criterion.values()) {
            for (Eligibility e : Eligibility.values()) {
                metrics.get(c).get(e).add(addend.metrics.get(c).get(e));
            }
        }
    }

    /**
     * Averages a previously added object, effectively dividing all metrics by divisor.
     *
     * @param divisor
     */
    @Override
    public void divideBy(double divisor) {
        for (Criterion c : Criterion.values()) {
            for (Eligibility e : Eligibility.values()) {
                metrics.get(c).get(e).divideBy(divisor);
            }
        }
    }

    public double getOfficialRankingMeasure() {
        return getOfficialRankingMeasureByCriterion(Criterion.OVERALL_MICRO);
    }

    public class OfficialMetrics {
        public double precision = 0;
        public double recall = 0;
        public double specificity = 0;
        public double f1 = 0;
        public double auc = 0;

        /**
         * Adds a given OfficialMetrics object to the current one.
         *
         * @param addend
         */
        public void add(OfficialMetrics addend) {
            this.precision += addend.precision;
            this.recall += addend.recall;
            this.specificity += addend.specificity;
            this.f1 += addend.f1;
            this.auc += addend.auc;
        }

        /**
         * Divide all metrics by a given divisor. Used for averaging over folds in cross-validation.
         *
         * @param divisor
         */
        public void divideBy(double divisor) {
            this.precision /= divisor;
            this.recall /= divisor;
            this.specificity /= divisor;
            this.f1 /= divisor;
            this.auc /= divisor;
        }
    }

}
