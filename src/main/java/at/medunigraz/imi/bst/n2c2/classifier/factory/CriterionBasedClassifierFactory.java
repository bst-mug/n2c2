package at.medunigraz.imi.bst.n2c2.classifier.factory;

import at.medunigraz.imi.bst.n2c2.classifier.CriterionBasedClassifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CriterionBasedClassifierFactory implements ClassifierFactory<CriterionBasedClassifier> {

    protected final Map<Criterion, CriterionBasedClassifier> classifierByCriterion = new HashMap<>();

    protected CriterionBasedClassifierFactory() {
        // Only used by subclasses
    }

    public CriterionBasedClassifierFactory(Class cls) {
        Arrays.stream(Criterion.classifiableValues()).forEach(c -> {
            try {
                classifierByCriterion.put(c, (CriterionBasedClassifier) cls.getDeclaredConstructor(Criterion.class).newInstance(c));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CriterionBasedClassifier getClassifier(Criterion criterion) {
        return classifierByCriterion.get(criterion);
    }

    @Override
    public String toString() {
        // Call toString on the firstElement
        return classifierByCriterion.values().iterator().next().toString();
    }
}
