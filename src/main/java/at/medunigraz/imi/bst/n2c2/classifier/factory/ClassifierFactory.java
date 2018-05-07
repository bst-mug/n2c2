package at.medunigraz.imi.bst.n2c2.classifier.factory;

import at.medunigraz.imi.bst.n2c2.classifier.Classifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.List;

public interface ClassifierFactory {

    Classifier getClassifier(Criterion criterion);

    default List<Patient> trainAndPredict(List<Patient> train, List<Patient> toPredict) {
        List<Patient> prediction = toPredict;

        for (Criterion criterion : Criterion.classifiableValues()) {
            Classifier classifier = this.getClassifier(criterion);
            classifier.train(train);
            prediction = classifier.predict(prediction);
        }

        return prediction;
    }
}
