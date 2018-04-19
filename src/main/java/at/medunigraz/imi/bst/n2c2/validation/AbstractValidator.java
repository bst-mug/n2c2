package at.medunigraz.imi.bst.n2c2.validation;

import at.medunigraz.imi.bst.n2c2.classifier.Classifier;
import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.metrics.Metrics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public abstract class AbstractValidator implements Validator {

    private static final Logger LOG = LogManager.getLogger();

    protected List<Patient> patients;
    protected ClassifierFactory classifierFactory;
    protected Evaluator evaluator;

    public AbstractValidator(List<Patient> patients, ClassifierFactory classifierFactory, Evaluator evaluator) {
        this.patients = patients;
        this.classifierFactory = classifierFactory;
        this.evaluator = evaluator;
    }

    protected Metrics validateFold(List<Patient> train, List<Patient> test, List<Patient> gold) {
        List<Patient> predicted = test;

        for (Criterion c : Criterion.classifiableValues()) {
            LOG.info("Evaluating criterion {}...", c);
            // So far, neural nets classify in a single pass all eligibility criteria
            // Therefore, we need *one* of the following:
            // FIXME (A) Make neural nets resilient to multiple calls to predict() - maybe check if the patientID was already predicted and just return a cached copy
            // FIXME (B) The current method detects all eligibility criteria are set and stops early.
            // michel 20180416 (B) makes more sense to me

            Classifier classifier = classifierFactory.getClassifier(c);

            classifier.train(train);
            predicted = classifier.predict(predicted);
        }

        evaluator.evaluate(gold, predicted);

        return evaluator.getMetrics();
    }

}
