package at.medunigraz.imi.bst.n2c2.classifier.factory;

import at.medunigraz.imi.bst.n2c2.classifier.Classifier;
import at.medunigraz.imi.bst.n2c2.classifier.MajorityClassifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MajorityClassifierFactoryTest {

    @Test
    public void getClassifier() {
        ClassifierFactory factory = FactoryProvider.getMajorityFactory();
        Classifier c = factory.getClassifier(Criterion.ABDOMINAL);
        assertTrue(c instanceof MajorityClassifier);
    }
}