package at.medunigraz.imi.bst.n2c2.classifier.factory;

import at.medunigraz.imi.bst.n2c2.classifier.FakeClassifier;
import at.medunigraz.imi.bst.n2c2.classifier.MajorityClassifier;
import at.medunigraz.imi.bst.n2c2.classifier.PerceptronClassifier;
import at.medunigraz.imi.bst.n2c2.classifier.svm.SVMClassifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.nn.BiLSTMCharacterTrigramClassifier;
import at.medunigraz.imi.bst.n2c2.nn.LSTMPreTrainedEmbeddingsClassifier;
import at.medunigraz.imi.bst.n2c2.nn.LSTMSelfTrainedEmbeddingsClassifier;
import at.medunigraz.imi.bst.n2c2.rules.RuleBasedClassifier;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FactoryProviderTest {

    @Test
    public void getClassifier() {
        assertTrue(FactoryProvider.getMajorityFactory().getClassifier(Criterion.ABDOMINAL) instanceof MajorityClassifier);
        assertTrue(FactoryProvider.getRBCFactory().getClassifier(Criterion.ABDOMINAL) instanceof RuleBasedClassifier);
        assertTrue(FactoryProvider.getSVMFactory().getClassifier(Criterion.ABDOMINAL) instanceof SVMClassifier);
        assertTrue(FactoryProvider.getPreTrainedPerceptronFactory().getClassifier(Criterion.ABDOMINAL) instanceof PerceptronClassifier);
        assertTrue(FactoryProvider.getSelfTrainedPerceptronFactory().getClassifier(Criterion.ABDOMINAL) instanceof PerceptronClassifier);
        assertTrue(FactoryProvider.getBiLSTMCharacterTrigramFactory().getClassifier(Criterion.ABDOMINAL) instanceof BiLSTMCharacterTrigramClassifier);
        assertTrue(FactoryProvider.getLSTMPreTrainedFactory().getClassifier(Criterion.ABDOMINAL) instanceof LSTMPreTrainedEmbeddingsClassifier);
        assertTrue(FactoryProvider.getLSTMSelfTrainedFactory().getClassifier(Criterion.ABDOMINAL) instanceof LSTMSelfTrainedEmbeddingsClassifier);
        assertTrue(FactoryProvider.getFakeClassifierFactory().getClassifier(Criterion.ABDOMINAL) instanceof FakeClassifier);
    }
}