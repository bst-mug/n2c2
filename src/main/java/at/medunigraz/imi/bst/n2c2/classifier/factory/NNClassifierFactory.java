package at.medunigraz.imi.bst.n2c2.classifier.factory;

import at.medunigraz.imi.bst.n2c2.classifier.Classifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.nn.BILSTMC3GClassifier;

public class NNClassifierFactory implements ClassifierFactory {

    private static final Classifier classifier = new BILSTMC3GClassifier();

	@Override
	public Classifier getClassifier(Criterion criterion) {
		return classifier;
	}
}
