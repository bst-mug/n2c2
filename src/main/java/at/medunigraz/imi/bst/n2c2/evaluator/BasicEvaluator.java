package at.medunigraz.imi.bst.n2c2.evaluator;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.metrics.BasicMetricSet;
import at.medunigraz.imi.bst.n2c2.model.metrics.BasicMetrics;
import at.medunigraz.imi.bst.n2c2.model.metrics.Metrics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BasicEvaluator implements Evaluator {

    private static final Logger LOG = LogManager.getLogger();

    private BasicMetricSet metricSet = new BasicMetricSet();

    @Override
    public void evaluate(List<Patient> gold, List<Patient> results) {
        // TODO consolidate validate and getMetrics() into a single method and drop deprecated methods so this is a pure function.
        metricSet = new BasicMetricSet();

        BasicMetrics metrics = new BasicMetrics(0);
        BasicMetrics macroMetrics = new BasicMetrics(0);

        // Map of results by patient id for comparison
        Map<String, Patient> resultsMap = results.stream().collect(Collectors.toMap(Patient::getID, p -> p));

        for (Criterion criterion : Criterion.classifiableValues()) {
            int tp = 0;
            int fp = 0;
            int tn = 0;
            int fn = 0;

            // TODO parallel stream
            for (Patient g : gold) {
                Patient actual = resultsMap.get(g.getID());

                Match match = comparePatients(g, actual, criterion);
                switch (match) {
                    case TP:
                        tp++;
                        break;
                    case FP:
                        fp++;
                        break;
                    case TN:
                        tn++;
                        break;
                    case FN:
                        fn++;
                        break;
                    case UNKNOWN:
                        LOG.warn("Unknown match between gold patient {} and actual patient {} when comparing criterion {}.",
                            g.getID(), actual.getID(), criterion);
                        break;
                }
            }

            BasicMetrics criterionMetrics = new BasicMetrics(tp, fp, tn, fn);

            metricSet.withBasicMetrics(criterion, criterionMetrics);

            metrics.add(criterionMetrics);
            macroMetrics.add(criterionMetrics);
        }

        // Micro-averaged metrics depend on lazy calculation of accuracy.
        // Conversely, macro-averaged metrics should be calculated now.
        macroMetrics.getAccuracy();

        metricSet.withBasicMetrics(Criterion.OVERALL_MACRO, macroMetrics);
        metricSet.withBasicMetrics(Criterion.OVERALL_MICRO, metrics);
    }

    @Override
    public Metrics getMetrics() {
        return metricSet;
    }

    private enum Match {
        TP, FP, TN, FN, UNKNOWN
    }

    private Match comparePatients(Patient gold, Patient actual, Criterion criterion) {
        if (Eligibility.classifiableValues().length != 2) {
            throw new UnsupportedOperationException("Multi-class comparison is not supported.");
        }

        if (gold.getEligibility(criterion) == Eligibility.MET) {
            if (actual.getEligibility(criterion) == Eligibility.MET) {
                return Match.TP;
            } else if (actual.getEligibility(criterion) == Eligibility.NOT_MET) {
                LOG.debug("Got a false negative (FN) for {} in {}", criterion, actual.getID());
                return Match.FN;
            }
        } else if (gold.getEligibility(criterion) == Eligibility.NOT_MET) {
            if (actual.getEligibility(criterion) == Eligibility.MET) {
                LOG.debug("Got a false positive (FP) for {} in {}", criterion, actual.getID());
                return Match.FP;
            } else if (actual.getEligibility(criterion) == Eligibility.NOT_MET) {
                return Match.TN;
            }
        }

        return Match.UNKNOWN;
    }
}
