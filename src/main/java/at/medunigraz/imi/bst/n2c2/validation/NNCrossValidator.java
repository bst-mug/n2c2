package at.medunigraz.imi.bst.n2c2.validation;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.dataset.CrossValidatedDataset;
import at.medunigraz.imi.bst.n2c2.model.metrics.Metrics;
import at.medunigraz.imi.bst.n2c2.nn.BILSTMC3GClassifier;

public class NNCrossValidator extends CrossValidator {

	private static final Logger LOG = LogManager.getLogger();

	public NNCrossValidator(List<Patient> patients, ClassifierFactory classifierFactory, Evaluator evaluator) {
		super(patients, classifierFactory, evaluator);
	}

	public Metrics validate() {
		return validate(CrossValidatedDataset.DEFAULT_FOLDS);
	}

	@Override
	public Metrics validate(int k) {
		Metrics metrics = null;

		CrossValidatedDataset dataset = new CrossValidatedDataset(patients);
		dataset.splitIntoFolds(k);

		for (int i = 0; i < k; i++) {
			LOG.info("Evaluating fold {}/{}...", i + 1, k);

			dataset.setCurrentFold(i);

			List<Patient> train = dataset.getTrainingSet();
			List<Patient> test = dataset.getTestSet();
			List<Patient> gold = dataset.getGoldSet();

			Metrics foldMetrics = validateFold(train, test, gold);

			// First initialization
			if (metrics == null) {
				metrics = foldMetrics;
			} else {
				metrics.add(foldMetrics);
			}

			// reset after fold
			((BILSTMC3GClassifier) classifierFactory.getClassifier(Criterion.classifiableValues()[0]))
					.setTrained(false);
		}

		metrics.divideBy(k);

		return metrics;
	}
}
