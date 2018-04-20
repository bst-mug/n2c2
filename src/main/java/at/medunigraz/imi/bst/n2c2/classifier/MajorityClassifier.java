package at.medunigraz.imi.bst.n2c2.classifier;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MajorityClassifier extends CriterionBasedClassifier {

    private Map<Eligibility, Integer> countPerEligibility = new HashMap<>();

    public MajorityClassifier(Criterion c) {
        super(c);
        reset();
    }

    private void reset() {
        for (Eligibility e : Eligibility.classifiableValues()) {
            countPerEligibility.put(e, 0);
        }
    }

    @Override
    public void train(List<Patient> examples) {
        reset();

        for (Patient p : examples) {
            Eligibility e = p.getEligibility(criterion);
            // e may be null during test
            if (e != null) {
                countPerEligibility.compute(e, (key, value) -> value += 1);
            }
        }
    }

    @Override
    public Eligibility predict(Patient p) {
        return countPerEligibility.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
    }
}
