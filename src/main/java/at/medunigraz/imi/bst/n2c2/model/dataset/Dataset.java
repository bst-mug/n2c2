package at.medunigraz.imi.bst.n2c2.model.dataset;

import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.List;

public interface Dataset {

    void split();

    /**
     * Returns the training set, WITH annotations.
     *
     * @return
     */
    List<Patient> getTrainingSet();

    /**
     * Returns the validation set, WITH annotations.
     *
     * @return
     */
    List<Patient> getValidationSet();

    /**
     * Returns the test set WITHOUT annotations.
     *
     * @return
     */
    List<Patient> getTestSet();

    /**
     * Returns the test set WITH annotations.
     *
     * @return
     */
    List<Patient> getGoldSet();
}
