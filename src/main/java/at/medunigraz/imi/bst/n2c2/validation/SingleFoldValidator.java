package at.medunigraz.imi.bst.n2c2.validation;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.dataset.CrossValidatedDataset;
import at.medunigraz.imi.bst.n2c2.model.dataset.Dataset;
import at.medunigraz.imi.bst.n2c2.model.metrics.Metrics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class SingleFoldValidator extends AbstractValidator {

    private static final Logger LOG = LogManager.getLogger();

    public SingleFoldValidator(List<Patient> patients, ClassifierFactory classifierFactory, Evaluator evaluator) {
        super(patients, classifierFactory, evaluator);
    }

    public Metrics validate() {
        return validate(Dataset.DEFAULT_TEST_SET_PORCENTAGE);
    }

    public Metrics validate(float testSetPercentage) {
        Metrics metrics = null;

        // FIXME split dataset accordingly. maybe use a fixed list Markus will provide
        testSetPercentage = 10;

        // FIXME draft, but working code: split into 10 folds, but do not cross-validate
        // 1 fold is then 10% testSetPercentage
        CrossValidatedDataset dataset = new CrossValidatedDataset(patients);
        dataset.splitIntoFolds(10);

        LOG.info("Evaluating with {}% validation set...", testSetPercentage);
        List<Patient> train = dataset.getTrainingSet(0);
        List<Patient> test = dataset.getTestSet(0);
        List<Patient> gold = dataset.getGoldSet(0);

        return validateFold(train, test, gold);
    }
}
