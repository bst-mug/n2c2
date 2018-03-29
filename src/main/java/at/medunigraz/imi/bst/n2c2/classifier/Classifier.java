package at.medunigraz.imi.bst.n2c2.classifier;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.List;

public interface Classifier {

    void train(List<Patient> examples);

    @Deprecated
    Eligibility predict(Patient p);

    Eligibility predict(Patient p, Criterion c);

    List<Patient> predict(List<Patient> patientList);
}
