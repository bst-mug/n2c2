package at.medunigraz.imi.bst.n2c2.model.dataset;

import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.List;

public abstract class AbstractDataset implements Dataset {

    protected List<Patient> patients;

    public AbstractDataset(List<Patient> patientList) {
        this.patients = patientList;
    }

}
