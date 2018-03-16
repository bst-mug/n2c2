package at.medunigraz.imi.bst.n2c2.model;

import org.w3c.dom.Element;

public class Tag {

    private String name;
    private Criterion criterion;

    private Tag() {
    }

    public static Tag fromElement(Element element) {
        Tag c = new Tag();

        String name = element.getTagName();
        c.withName(name);

        String met = element.getAttribute("met");
        if (met.equals("met")) {
            c.withCriterion(Criterion.MET);
        } else if (met.equals("not met")) {
            c.withCriterion(Criterion.NOT_MET);
        }

        return c;
    }

    private Tag withName(String name) {
        this.name = name;
        return this;
    }

    private Tag withCriterion(Criterion criterion) {
        this.criterion = criterion;
        return this;
    }

    public String getName() {
        return name;
    }

    public Criterion getCriterion() {
        return criterion;
    }
}
