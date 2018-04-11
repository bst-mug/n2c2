package at.medunigraz.imi.bst.n2c2.validation;

import at.medunigraz.imi.bst.n2c2.classifier.Classifier;
import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.util.Dataset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CrossValidator {

    private static final Logger LOG = LogManager.getLogger();

    private List<Patient> patients;
    private ClassifierFactory classifierFactory;
    private Evaluator evaluator;

    public CrossValidator(List<Patient> patients, ClassifierFactory classifierFactory, Evaluator evaluator) {
        this.patients = patients;
        this.classifierFactory = classifierFactory;
        this.evaluator = evaluator;
    }

    public Map<Criterion, Double> evaluate() {
        return evaluate(Dataset.DEFAULT_FOLDS);
    }

    public Map<Criterion, Double> evaluate(int k) {
        List<Map<Criterion, Double>> metrics = new ArrayList<>();

        Dataset dataset = new Dataset(patients);
        dataset.splitIntoFolds(k);

        for (int i = 0; i < k; i++) {
            LOG.info("Evaluating fold {}/{}...", i + 1, k);
            List<Patient> train = dataset.getTrainingSet(i);
            List<Patient> test = dataset.getTestSet(i);
            List<Patient> gold = dataset.getGoldSet(i);

            metrics.add(evaluateFold(train, test, gold));
        }

        return metrics.stream().flatMap(e -> e.entrySet().stream()).collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.averagingDouble(Map.Entry::getValue)));
    }

    private Map<Criterion, Double> evaluateFold(List<Patient> train, List<Patient> test, List<Patient> gold) {
        for (Criterion c : Criterion.classifiableValues()) {
            LOG.info("Evaluating criterion {}...", c);
            Classifier classifier = classifierFactory.getClassifier(c);

            classifier.train(train);
            test = classifier.predict(test);
        }

        evaluator.evaluate(gold, test);

        Map<Criterion, Double> ret = new HashMap<>();
        for (Criterion c : Criterion.values()) {
            ret.put(c, evaluator.getOfficialRankingMeasureByCriterion(c));
        }
        return ret;
    }
}
