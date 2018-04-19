package at.medunigraz.imi.bst.n2c2.model.dataset;

import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.List;

public class Dataset {

    public static final float DEFAULT_TEST_SET_PORCENTAGE = 10;

    protected List<Patient> patients;

    public Dataset(List<Patient> patientList) {
        this.patients = patientList;
    }


}
