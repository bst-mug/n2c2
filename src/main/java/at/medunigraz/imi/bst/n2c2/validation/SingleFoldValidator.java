package at.medunigraz.imi.bst.n2c2.validation;

import at.medunigraz.imi.bst.n2c2.classifier.Classifier;
import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.metrics.Metrics;
import at.medunigraz.imi.bst.n2c2.util.Dataset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class SingleFoldValidator extends AbstractValidator {

    private static final Logger LOG = LogManager.getLogger();

    public SingleFoldValidator(List<Patient> patients, ClassifierFactory classifierFactory, Evaluator evaluator) {
        super(patients, classifierFactory, evaluator);
    }

    public Metrics validate() {
        return validate(10);
    }

    public Metrics validate(float validationSetPercentage) {
        Metrics metrics = null;

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

        return evaluateFold(train, test, gold);
    }

    private Metrics evaluateFold(List<Patient> train, List<Patient> test, List<Patient> gold) {
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

        return evaluator.getMetrics();
    }
}
