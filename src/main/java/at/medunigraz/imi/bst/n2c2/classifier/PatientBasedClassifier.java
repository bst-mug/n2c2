package at.medunigraz.imi.bst.n2c2.classifier;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.Arrays;
import java.util.List;

public abstract class PatientBasedClassifier implements Classifier {

    @Override
    public List<Patient> predict(List<Patient> patientList) {
        patientList.forEach(p -> Arrays.stream(Criterion.classifiableValues()).forEach(c -> p.withCriterion(c, this.predictIfNecessary(p, c))));
        return patientList;
    }

    private Eligibility predictIfNecessary(Patient p, Criterion c) {
        if (p.hasEligibility(c)) {
            return p.getEligibility(c);
        }

        return predict(p, c);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
