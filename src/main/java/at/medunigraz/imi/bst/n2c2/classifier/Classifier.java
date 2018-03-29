package at.medunigraz.imi.bst.n2c2.classifier;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface Classifier {

    void train(List<Patient> examples);

    Eligibility predict(Patient p);

    default Map<Patient, Eligibility> predict(List<Patient> patientList) {
        return patientList.stream().collect(Collectors.toMap(p -> p, p -> predict(p)));
    }
}
