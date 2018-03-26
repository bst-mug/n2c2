package at.medunigraz.imi.bst.n2c2.model;

import java.util.HashMap;
import java.util.Map;

public class Patient {

    private String text;
    private Map<Criterion, Eligibility> criteria = new HashMap<>();

    public Patient withText(String text) {
        this.text = text;
        return this;
    }

    public Patient withCriterion(Criterion criterion, Eligibility eligibility) {
        criteria.put(criterion, eligibility);
        return this;
    }

    public String getText() {
        return text;
    }

    public Eligibility getEligibility(Criterion criterion) {
        return criteria.get(criterion);
    }
}
