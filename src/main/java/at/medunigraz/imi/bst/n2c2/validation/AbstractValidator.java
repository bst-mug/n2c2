package at.medunigraz.imi.bst.n2c2.validation;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.List;

public abstract class AbstractValidator implements Validator {

    protected List<Patient> patients;
    protected ClassifierFactory classifierFactory;
    protected Evaluator evaluator;

    public AbstractValidator(List<Patient> patients, ClassifierFactory classifierFactory, Evaluator evaluator) {
        this.patients = patients;
        this.classifierFactory = classifierFactory;
        this.evaluator = evaluator;
    }

}
