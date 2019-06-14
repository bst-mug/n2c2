package at.medunigraz.imi.bst.n2c2.classifier.factory;

import at.medunigraz.imi.bst.n2c2.classifier.FakeClassifier;
import at.medunigraz.imi.bst.n2c2.classifier.MajorityClassifier;
import at.medunigraz.imi.bst.n2c2.classifier.PatientBasedClassifier;
import at.medunigraz.imi.bst.n2c2.nn.BiLSTMCharacterTrigramClassifier;
import at.medunigraz.imi.bst.n2c2.nn.LSTMPreTrainedEmbeddingsClassifier;
import at.medunigraz.imi.bst.n2c2.nn.LSTMSelfTrainedEmbeddingsClassifier;
import at.medunigraz.imi.bst.n2c2.rules.RuleBasedClassifier;

public abstract class FactoryProvider {

    public static CriterionBasedClassifierFactory getMajorityFactory() {
        return new CriterionBasedClassifierFactory(MajorityClassifier.class);
    }

    public static CriterionBasedClassifierFactory getRBCFactory() {
        return new CriterionBasedClassifierFactory(RuleBasedClassifier.class);
    }

    public static CriterionBasedClassifierFactory getSVMFactory() {
        return new SVMClassifierFactory();
    }

    public static CriterionBasedClassifierFactory getSelfTrainedPerceptronFactory() {
        return new PerceptronClassifierFactory();
    }

    public static CriterionBasedClassifierFactory getPreTrainedPerceptronFactory() {
        return new PerceptronClassifierFactory(true);
    }

    public static PatientBasedClassifierFactory getBiLSTMCharacterTrigramFactory() {
        return new PatientBasedClassifierFactory(BiLSTMCharacterTrigramClassifier.class);
    }

    public static ClassifierFactory<PatientBasedClassifier> getLSTMPreTrainedFactory() {
        return new PatientBasedClassifierFactory(LSTMPreTrainedEmbeddingsClassifier.class);
    }

    public static PatientBasedClassifierFactory getLSTMSelfTrainedFactory() {
        return new PatientBasedClassifierFactory(LSTMSelfTrainedEmbeddingsClassifier.class);
    }

    public static CriterionBasedClassifierFactory getFakeClassifierFactory() {
        return new CriterionBasedClassifierFactory(FakeClassifier.class);
    }
}
