package at.medunigraz.imi.bst.n2c2.classifier.factory;

import at.medunigraz.imi.bst.n2c2.classifier.Classifier;
import at.medunigraz.imi.bst.n2c2.classifier.MajorityClassifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BestClassifierFactory implements ClassifierFactory {

    private static final Map<Criterion, Classifier> classifierByCriterion = new HashMap<>();

    static {
        Arrays.stream(Criterion.values()).forEach(c -> classifierByCriterion.put(c, new MajorityClassifier(c)));
    }

    @Override
    public Classifier getClassifier(Criterion criterion) {
        return classifierByCriterion.get(criterion);
    }
}
