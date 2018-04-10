package at.medunigraz.imi.bst.n2c2.classifier.svm;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.SVMClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.BasicEvaluator;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import at.medunigraz.imi.bst.n2c2.validation.CrossValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SVMCostOptimizer {

    private static final Logger LOG = LogManager.getLogger();
    private static Map<Criterion, CostMetric> bestCostPerCriterion = new HashMap<>();

    static {
        Arrays.stream(Criterion.values()).forEach(c -> bestCostPerCriterion.put(c, new CostMetric()));
    }

    public static void main(String[] args) {
        final File dataFolder = new File("data");

        List<Patient> patients = DatasetUtil.loadFromFolder(dataFolder);
        Evaluator evaluator = new BasicEvaluator();

        for (int exp = -20; exp <= 20; exp += 0.5) {
            double cost = Math.pow(2, exp);

            ClassifierFactory factory = new SVMClassifierFactory(cost);
            CrossValidator cv = new CrossValidator(patients, factory, evaluator);
            Map<Criterion, Double> metrics = cv.evaluate();

            for (Map.Entry<Criterion, Double> entry : metrics.entrySet()) {
                updateBestCosts(entry.getKey(), entry.getValue(), exp);
            }

            LOG.info("exp = {}", exp);
            LOG.info(bestCostPerCriterion);
        }
    }

    private static void updateBestCosts(Criterion criterion, double metric, float exp) {
        CostMetric costMetric = bestCostPerCriterion.get(criterion);

        if (metric > costMetric.metric) {
            costMetric.metric = metric;
            costMetric.exp = exp;
        }
    }

    private static class CostMetric {
        private float exp = 0;
        private double metric = 0;

        @Override
        public String toString() {
            return String.format("C = 2^%f = %f => A = %f", exp, Math.pow(2, exp), metric);
        }
    }
}
