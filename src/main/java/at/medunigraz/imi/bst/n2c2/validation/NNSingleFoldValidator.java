package at.medunigraz.imi.bst.n2c2.validation;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.dataset.SingleFoldValidatedDataset;
import at.medunigraz.imi.bst.n2c2.model.metrics.Metrics;

public class NNSingleFoldValidator extends SingleFoldValidator {

	private static final Logger LOG = LogManager.getLogger();

	public NNSingleFoldValidator(List<Patient> patients, ClassifierFactory classifierFactory, Evaluator evaluator) {
		super(patients, classifierFactory, evaluator);
	}

	@Override
	public Metrics validate() {
		SingleFoldValidatedDataset dataset = new SingleFoldValidatedDataset(patients);
		dataset.split(0.9, 0, 0.1);

		List<Patient> train = dataset.getTrainingSet();
		List<Patient> test = dataset.getTestSet();
		List<Patient> gold = dataset.getGoldSet();

		return validateFold(train, test, gold);
	}
}
