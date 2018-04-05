package at.medunigraz.imi.bst.n2c2.evaluator;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Metrics;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BasicEvaluator extends AbstractEvaluator {

    private enum Match {
        TP, FP, TN, FN, UNKNOWN;
    }

    private Map<Criterion, Metrics> metricsByCriterion = new HashMap<>();

    @Override
    public void evaluate(List<Patient> gold, List<Patient> results) {
        int count = 0;
        double overallPrecision = 0, overallRecall = 0;

        // Map of results by patient id for comparison
        Map<String, Patient> resultsMap = results.stream().collect(Collectors.toMap(Patient::getID, p -> p));

        for (Criterion criterion: Criterion.values()) {
            int tp = 0, fp = 0, tn = 0, fn = 0;

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
                }
            }

            Metrics metrics = new Metrics(tp, fp, tn, fn);
            if (!metrics.isValid()) {
                continue;
            }

            overallPrecision += metrics.getPrecision();
            overallRecall += metrics.getRecall();
            count++;

            metricsByCriterion.put(criterion, metrics);
        }

        metricsByCriterion.put(Criterion.OVERALL, new Metrics(overallPrecision / count, overallRecall / count));
    }

    @Override
    public double getF1ByCriterion(Criterion c) {
        return metricsByCriterion.get(c).getF1();
    }

    private Match comparePatients(Patient gold, Patient actual, Criterion criterion) {
        if (Eligibility.values().length != 2) {
            throw new UnsupportedOperationException("Multi-class comparison is not supported.");
        }

        if (gold.getEligibility(criterion) == Eligibility.MET) {
            if (actual.getEligibility(criterion) == Eligibility.MET) {
                return Match.TP;
            } else if (actual.getEligibility(criterion) == Eligibility.NOT_MET) {
                return Match.FN;
            }
        } else if (gold.getEligibility(criterion) == Eligibility.NOT_MET) {
            if (actual.getEligibility(criterion) == Eligibility.MET) {
                return Match.FP;
            } else if (actual.getEligibility(criterion) == Eligibility.NOT_MET) {
                return Match.TN;
            }
        }

        return Match.UNKNOWN;
    }
}
