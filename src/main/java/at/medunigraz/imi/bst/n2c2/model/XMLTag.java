package at.medunigraz.imi.bst.n2c2.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XMLTag {

    private static final String MET_NAME = "met";

    private Criterion criterion;
    private Eligibility eligibility;

    public static XMLTag fromElement(Element element) {
        XMLTag c = new XMLTag();

        String name = element.getTagName();
        c.withCriterion(Criterion.get(name));

        String met = element.getAttribute(MET_NAME);
        c.withEligibility(Eligibility.get(met));

        return c;
    }

    public Element toElement(Document doc) {
        Element element = doc.createElement(criterion.toString());
        element.setAttribute(MET_NAME, eligibility.toString());
        return element;
    }

    public XMLTag withCriterion(Criterion criterion) {
        this.criterion = criterion;
        return this;
    }

    public XMLTag withEligibility(Eligibility eligibility) {
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
