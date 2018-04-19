package at.medunigraz.imi.bst.n2c2.model.dataset;

import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.List;

public class SingleFoldValidatedDataset extends Dataset {

    public static final float DEFAULT_TEST_SET_PORCENTAGE = 10;

    public SingleFoldValidatedDataset(List<Patient> patientList) {
        super(patientList);
    }
}
