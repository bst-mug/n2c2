package at.medunigraz.imi.bst.n2c2.classifier.factory;

import at.medunigraz.imi.bst.n2c2.classifier.PatientBasedClassifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;


public final class PatientBasedClassifierFactory implements ClassifierFactory<PatientBasedClassifier> {

    private final PatientBasedClassifier classifier;

    public PatientBasedClassifierFactory(Class cls) {
        try {
            classifier = (PatientBasedClassifier) cls.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PatientBasedClassifier getClassifier(Criterion criterion) {
        return classifier;
    }

}
