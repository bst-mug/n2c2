package at.medunigraz.imi.bst.n2c2.validation;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.dataset.SingleFoldValidatedDataset;
import at.medunigraz.imi.bst.n2c2.model.metrics.Metrics;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class TrainingValidator extends AbstractValidator {

    private static final Logger LOG = LogManager.getLogger();

    private static final double DEFAULT_TRAINING_SET_PERCENTAGE = 0.9;
    private static final double DEFAULT_TEST_SET_PERCENTAGE = 0.1;

    public TrainingValidator(List<Patient> patients, ClassifierFactory classifierFactory, Evaluator evaluator) {
        super(patients, classifierFactory, evaluator);
    }

    public Metrics validate(double trainPercentage, double testPercentage) {
        SingleFoldValidatedDataset trainDataset = new SingleFoldValidatedDataset(patients);

        // We use the validation set as a placeholder for the remaining patients
        trainDataset.split(trainPercentage, 1.0 - trainPercentage - testPercentage, testPercentage);
        List<Patient> train = trainDataset.getTrainingSet();

        SingleFoldValidatedDataset testDataset = new SingleFoldValidatedDataset(patients);

        // We here abuse the SingleFoldValidatedDataset to create a "test" set out of all training data
        testDataset.split(1.0 - testPercentage, 0, testPercentage);
        List<Patient> gold = testDataset.getTrainingSet();
        List<Patient> test = DatasetUtil.stripTags(gold);

        return validateFold(train, test, gold);
    }

    public Metrics validate() {
        return validate(DEFAULT_TRAINING_SET_PERCENTAGE, DEFAULT_TEST_SET_PERCENTAGE);
    }
}
