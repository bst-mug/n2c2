package at.medunigraz.imi.bst.n2c2.classifier;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.List;

public abstract class CriterionBasedClassifier implements Classifier {

    protected Criterion criterion;

    public CriterionBasedClassifier(Criterion c) {
        this.criterion = c;
    }

    @Override
    public List<Patient> predict(List<Patient> patientList) {
        patientList.forEach(p -> p.withCriterion(criterion, predict(p)));
        return patientList;
    }

    @Override
    public Eligibility predict(Patient p, Criterion c) {
        if (!c.equals(criterion)) {
            throw new UnsupportedOperationException("Criterion must be consistent with constructor!");
        }
        return predict(p);
    }

    public abstract Eligibility predict(Patient p);
}
