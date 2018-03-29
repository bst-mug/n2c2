package at.medunigraz.imi.bst.n2c2.classifier.factory;

import at.medunigraz.imi.bst.n2c2.classifier.MajorityClassifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BaselineClassifierFactoryTest {

    @Test
    public void getClassifier() {
        BaselineClassifierFactory factory = new BaselineClassifierFactory();
        assertTrue(factory.getClassifier(Criterion.ABDOMINAL) instanceof MajorityClassifier);
    }
}