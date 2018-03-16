package at.medunigraz.imi.bst.n2c2.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Patient {

    private String text;
    private Map<Criterion, Eligibility> criteria = new HashMap<>();

    private Patient() {

    }

    public static Patient fromXML(File xmlFile) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        Document doc = null;
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            doc = documentBuilder.parse(xmlFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        String text = doc.getElementsByTagName("TEXT").item(0).getTextContent();
        Patient patient = new Patient().withText(text);

        Element tagsElement = (Element) doc.getElementsByTagName("TAGS").item(0);

        NodeList tags = tagsElement.getElementsByTagName("*");
        for (int i = 0; i < tags.getLength(); i++) {
            Element element = (Element) tags.item(i);
            XMLTag t = XMLTag.fromElement(element);
            patient.withXMLTag(t);
        }

        return patient;
    }

    private Patient withText(String text) {
        this.text = text;
        return this;
    }

    private Patient withXMLTag(XMLTag tag) {
        return withCriterion(tag.getCriterion(), tag.getEligibility());
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
