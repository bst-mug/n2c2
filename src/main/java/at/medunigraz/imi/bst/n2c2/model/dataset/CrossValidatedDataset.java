package at.medunigraz.imi.bst.n2c2.model.dataset;

import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import smile.validation.CrossValidation;

import java.util.List;

public class CrossValidatedDataset extends Dataset {

    public static final int DEFAULT_FOLDS = 10;

    private CrossValidation cv = null;

    public CrossValidatedDataset(List<Patient> patientList) {
        super(patientList);
    }

    public void splitIntoFolds(int k) {
        cv = new CrossValidation(patients.size(), k);
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
}
