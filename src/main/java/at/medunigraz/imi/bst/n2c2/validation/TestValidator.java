package at.medunigraz.imi.bst.n2c2.validation;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.metrics.Metrics;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class TestValidator extends AbstractValidator {

    private static final Logger LOG = LogManager.getLogger();

    private List<Patient> goldPatients;

    public TestValidator(List<Patient> trainPatients, List<Patient> goldPatients, ClassifierFactory classifierFactory, Evaluator evaluator) {
        super(trainPatients, classifierFactory, evaluator);
        this.goldPatients = goldPatients;
    }

    public Metrics validate() {
        List<Patient> testPatients = DatasetUtil.stripTags(goldPatients);
        return validateFold(patients, testPatients, goldPatients);
    }
}
