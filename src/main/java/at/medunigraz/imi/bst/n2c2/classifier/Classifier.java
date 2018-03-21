package at.medunigraz.imi.bst.n2c2.classifier;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.List;

public interface Classifier {

    void train(List<Patient> examples);

    Eligibility predict(Patient p);
}
