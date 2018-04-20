package at.medunigraz.imi.bst.n2c2.model.dataset;

import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import smile.validation.CrossValidation;

import java.util.List;

public class CrossValidatedDataset extends AbstractDataset {

    private static final Logger LOG = LogManager.getLogger();

    public static final int DEFAULT_FOLDS = 10;

    private CrossValidation cv = null;
    private int currentFold;

    public CrossValidatedDataset(List<Patient> patientList) {
        super(patientList);
    }

    @Override
    public void split() {
        splitIntoFolds(DEFAULT_FOLDS);
    }

    public void splitIntoFolds(int k) {
        LOG.info("Splitting into {} folds...", k);
        cv = new CrossValidation(patients.size(), k);
    }

    public void setCurrentFold(int currentFold) {
        this.currentFold = currentFold;
    }

    public List<Patient> getTrainingSet(int fold) {
        int[] indices = getTrainFoldIndices(fold);
        return DatasetUtil.slice(patients, indices);
    }

    public List<Patient> getTestSet(int fold) {
        int[] indices = getTestFoldIndices(fold);
        List<Patient> strippedPatients = DatasetUtil.stripTags(patients);
        return DatasetUtil.slice(strippedPatients, indices);
    }

    public List<Patient> getGoldSet(int fold) {
        int[] indices = getTestFoldIndices(fold);
        return DatasetUtil.slice(patients, indices);
    }

    private int[] getTrainFoldIndices(int i) {
        if (cv == null) {
            splitIntoFolds(DEFAULT_FOLDS);
        }
        return cv.train[i];
    }

    private int[] getTestFoldIndices(int i) {
        if (cv == null) {
            splitIntoFolds(DEFAULT_FOLDS);
        }
        return cv.test[i];
    }

    @Override
    public List<Patient> getTrainingSet() {
        return getTrainingSet(currentFold);
    }

    @Override
    public List<Patient> getValidationSet() {
        throw new UnsupportedOperationException("Validation set is not implemented for cross-validation.");
    }

    @Override
    public List<Patient> getTestSet() {
        return getTestSet(currentFold);
    }

    /**
     * Returns the test set WITH annotations.
     *
     * @return
     */
    @Override
    public List<Patient> getGoldSet() {
        return getGoldSet(currentFold);
    }
}
