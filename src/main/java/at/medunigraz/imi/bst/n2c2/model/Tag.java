package at.medunigraz.imi.bst.n2c2.model;

import org.w3c.dom.Element;

public class Tag {

    private Criterion criterion;
    private Eligibility eligibility;

    private Tag() {
    }

    public static Tag fromElement(Element element) {
        Tag c = new Tag();

        String name = element.getTagName();
        c.withCriterion(Criterion.get(name));

        String met = element.getAttribute("met");
        c.withEligibility(Eligibility.get(met));

        return c;
    }

    private Tag withCriterion(Criterion criterion) {
        this.criterion = criterion;
        return this;
    }

    private Tag withEligibility(Eligibility eligibility) {
        this.eligibility = eligibility;
        return this;
    }

    public Criterion getCriterion() {
        return criterion;
    }

    public Eligibility getEligibility() {
        return eligibility;
    }
}
