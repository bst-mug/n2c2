package at.medunigraz.imi.bst.n2c2.model;

import java.util.HashMap;
import java.util.Map;

public class Patient {

    private String id;
    private String text;
    private Map<Criterion, Eligibility> criteria = new HashMap<>();

    public Patient withID(String id) {
        this.id = id;
        return this;
    }

    public Patient withText(String text) {
        this.text = text;
        return this;
    }

    public Patient withCriterion(Criterion criterion, Eligibility eligibility) {
        criteria.put(criterion, eligibility);
        return this;
    }

    public String getID() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Eligibility getEligibility(Criterion criterion) {
        return criteria.get(criterion);
    }
}
