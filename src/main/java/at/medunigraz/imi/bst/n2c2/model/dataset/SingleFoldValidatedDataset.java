package at.medunigraz.imi.bst.n2c2.model.dataset;

import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleFoldValidatedDataset extends AbstractDataset {

    public static final double DEFAULT_TRAINING_SET_PERCENTAGE = 0.8;
    public static final double DEFAULT_VALIDATION_SET_PERCENTAGE = 0.1;
    public static final double DEFAULT_TEST_SET_PERCENTAGE = 0.1;
    private static final Logger LOG = LogManager.getLogger();
    private Map<SplitType, List<Patient>> patientsPerSplit = null;

    public void split(double training, double validation, double test) {
        // We accept any of the following patterns (or variations thereof):
        // 0.8 + 0.0 + 0.2 = 1.0
        // 0.6 + 0.2 + 0.2 = 1.0
        Double sum = training + validation + test;
        // Delta to the sum should be less than 0.0.1
        if (Math.abs(sum - 1.0) > 0.01) {
            throw new UnsupportedOperationException("Invalid split ratios.");
        }
        LOG.info("Splitting into {}/{}/{} splits...", training, validation, test);

        int datasetSize = patients.size();

        int trainingSize = (int) (datasetSize * training);
        int validationSize = (int) (datasetSize * validation);
        int testSize = datasetSize - trainingSize - validationSize;

        int[] chosen = DatasetUtil.getRandomIndices(datasetSize, datasetSize);

        patientsPerSplit = new HashMap<>();
        patientsPerSplit.put(SplitType.TRAINING, DatasetUtil.slice(patients, chosen, 0, trainingSize));
        patientsPerSplit.put(SplitType.VALIDATION, DatasetUtil.slice(patients, chosen, trainingSize, validationSize));
        patientsPerSplit.put(SplitType.TEST, DatasetUtil.slice(patients, chosen, trainingSize + validationSize, testSize));
    }

    public SingleFoldValidatedDataset(List<Patient> patientList) {
        super(patientList);
    }

    @Override
    public void split() {
        split(DEFAULT_TRAINING_SET_PERCENTAGE, DEFAULT_VALIDATION_SET_PERCENTAGE, DEFAULT_TEST_SET_PERCENTAGE);
    }

    @Override
    public List<Patient> getTrainingSet() {
        if (patientsPerSplit == null) {
            split();
        }

        return patientsPerSplit.get(SplitType.TRAINING);
    }

    @Override
    public List<Patient> getValidationSet() {
        if (patientsPerSplit == null) {
            split();
        }

        return patientsPerSplit.get(SplitType.VALIDATION);
    }

    @Override
    public List<Patient> getTestSet() {
        List<Patient> gold = getGoldSet();
        List<Patient> test = DatasetUtil.stripTags(gold);

        return test;
    }

    /**
     * Returns the test set WITH annotations.
     *
     * @return
     */
    @Override
    public List<Patient> getGoldSet() {
        if (patientsPerSplit == null) {
            split();
        }

        return patientsPerSplit.get(SplitType.TEST);
    }

    public enum SplitType {
        TRAINING,
        VALIDATION,
        TEST
    }
}
