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

public class SingleFoldValidator {

    private static final Logger LOG = LogManager.getLogger();

    private List<Patient> patients;
    private ClassifierFactory classifierFactory;
    private Evaluator evaluator;

    public SingleFoldValidator(List<Patient> patients, ClassifierFactory classifierFactory, Evaluator evaluator) {
        this.patients = patients;
        this.classifierFactory = classifierFactory;
        this.evaluator = evaluator;
    }

    public Map<Criterion, Double> evaluate() {
        return evaluate(10);
    }

    public Map<Criterion, Double> evaluate(float validationSetPercentage) {
        Map<Criterion, Double> metrics = new HashMap<>();

        // FIXME split dataset accordingly. maybe use a fixed list Markus will provide
        validationSetPercentage = 10;

        // FIXME draft, but working code: split into 10 folds, but do not cross-validate
        // 1 fold is then 10% validationSetPercentage
        Dataset dataset = new Dataset(patients);
        dataset.splitIntoFolds(10);

        LOG.info("Evaluating with {}% validation set...", validationSetPercentage);
        List<Patient> train = dataset.getTrainingSet(0);
        List<Patient> test = dataset.getTestSet(0);
        List<Patient> gold = dataset.getGoldSet(0);

        metrics = evaluateFold(train, test, gold);

        return metrics;
    }

    private Map<Criterion, Double> evaluateFold(List<Patient> train, List<Patient> test, List<Patient> gold) {
        for (Criterion c : Criterion.classifiableValues()) {
            LOG.info("Evaluating criterion {}...", c);
            // So far, neural nets classify in a single pass all eligibility criteria
            // Therefore, we need *one* of the following:
            // FIXME (A) Make neural nets resilient to multiple calls to predict() - maybe check if the patientID was already predicted and just return a cached copy
            // FIXME (B) The current method detects all eligibility criteria are set and stops early.
            // michel 20180416 (B) makes more sense to me

            Classifier classifier = classifierFactory.getClassifier(c);
            classifier.train(train);
            test = classifier.predict(test);
        }

        evaluator.evaluate(gold, test);

        Map<Criterion, Double> ret = new HashMap<>();
        for (Criterion c : Criterion.values()) {
            // FIXME michel 20180416 get a MetricSet object
            ret.put(c, evaluator.getOfficialRankingMeasureByCriterion(c));
        }
        return ret;
    }
}
