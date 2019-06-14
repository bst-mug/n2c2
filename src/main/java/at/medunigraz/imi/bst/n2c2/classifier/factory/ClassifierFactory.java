package at.medunigraz.imi.bst.n2c2.classifier.factory;

import at.medunigraz.imi.bst.n2c2.classifier.Classifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;

import java.util.List;

public interface ClassifierFactory<C extends Classifier> {

    C getClassifier(Criterion criterion);

    default List<Patient> trainAndPredict(List<Patient> train, List<Patient> toPredict) {
        List<Patient> prediction = DatasetUtil.stripTags(toPredict);

        for (Criterion criterion : Criterion.classifiableValues()) {
            C classifier = this.getClassifier(criterion);
            classifier.train(train);
            prediction = classifier.predict(prediction);
        }

        return prediction;
    }
}
