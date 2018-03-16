package at.medunigraz.imi.bst.n2c2.model;

import org.w3c.dom.Element;

public class Tag {

    private String name;
    private Eligibility eligibility;

    private Tag() {
    }

    public static Tag fromElement(Element element) {
        Tag c = new Tag();

        String name = element.getTagName();
        c.withName(name);

        String met = element.getAttribute("met");
        if (met.equals("met")) {
            c.withEligibility(Eligibility.MET);
        } else if (met.equals("not met")) {
            c.withEligibility(Eligibility.NOT_MET);
        }

        return c;
    }

    private Tag withName(String name) {
        this.name = name;
        return this;
    }

    private Tag withEligibility(Eligibility eligibility) {
        this.eligibility = eligibility;
        return this;
    }

    public String getName() {
        return name;
    }

    public Eligibility getEligibility() {
        return eligibility;
    }
}
