package at.medunigraz.imi.bst.n2c2.validation;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
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

        predicted = classifierFactory.trainAndPredict(train, predicted);

        evaluator.evaluate(gold, predicted);

        return evaluator.getMetrics();
    }

}
