package at.medunigraz.imi.bst.n2c2.classifier.factory;

import at.medunigraz.imi.bst.n2c2.classifier.PerceptronClassifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;

import java.util.Arrays;

public class PerceptronClassifierFactory extends CriterionBasedClassifierFactory {

    public PerceptronClassifierFactory() {
        Arrays.stream(Criterion.classifiableValues()).forEach(c ->
                classifierByCriterion.put(c, new PerceptronClassifier(c, false)));
    }

    public PerceptronClassifierFactory(boolean preTrained) {
        Arrays.stream(Criterion.classifiableValues()).forEach(c ->
                classifierByCriterion.put(c, new PerceptronClassifier(c, preTrained)));
    }

}
