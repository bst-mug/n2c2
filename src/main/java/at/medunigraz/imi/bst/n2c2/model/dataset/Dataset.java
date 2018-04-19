package at.medunigraz.imi.bst.n2c2.model.dataset;

import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.List;

public class Dataset {

    protected List<Patient> patients;

    public Dataset(List<Patient> patientList) {
        this.patients = patientList;
    }


}
