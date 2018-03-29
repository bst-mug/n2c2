package at.medunigraz.imi.bst.n2c2.dao;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.XMLTag;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class PatientDAO {
    private static final String ROOT_NAME = "PatientMatching";
    private static final String TEXT_NAME = "TEXT";
    private static final String TAGS_NAME = "TAGS";

    private static DocumentBuilder documentBuilder = null;
    private static Transformer transformer = null;

    public PatientDAO() {
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        // TODO Lazy load, singleton
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public Patient fromXML(File xmlFile) throws IOException, SAXException {
        return fromXML(new FileInputStream(xmlFile), xmlFile.getName());
    }

    public Patient fromXML(InputStream xml, String id) throws IOException, SAXException {
        Document doc = documentBuilder.parse(xml);

        String text = doc.getElementsByTagName(TEXT_NAME).item(0).getTextContent();
        Patient patient = new Patient().withID(id).withText(text);

        Element tagsElement = (Element) doc.getElementsByTagName(TAGS_NAME).item(0);

        NodeList tags = tagsElement.getElementsByTagName("*");
        for (int i = 0; i < tags.getLength(); i++) {
            Element element = (Element) tags.item(i);
            XMLTag tag = XMLTag.fromElement(element);
            patient.withCriterion(tag.getCriterion(), tag.getEligibility());
        }

        return patient;
    }

    public void toXML(Patient patient, File outputFile) throws IOException {
        toXML(patient, new FileOutputStream(outputFile));
    }

    public void toXML(Patient patient, OutputStream outputStream) throws IOException {
        Document doc = documentBuilder.newDocument();

        Element rootElement = doc.createElement(ROOT_NAME);
        doc.appendChild(rootElement);

        Element textElement = doc.createElement(TEXT_NAME);
        textElement.appendChild(doc.createCDATASection(patient.getText()));
        rootElement.appendChild(textElement);

        Element tagsElement = doc.createElement(TAGS_NAME);
        rootElement.appendChild(tagsElement);

        for (Criterion c : Criterion.values()) {
            Eligibility e = patient.getEligibility(c);

            // Patient has no eligibility criterion set, should be found only during testing
            if (e == null) {
                continue;
            }

            Element criterionElement = new XMLTag().withCriterion(c).withEligibility(e).toElement(doc);
            tagsElement.appendChild(criterionElement);
        }

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(outputStream);

        writeHeader(result);
        writeDoc(doc, result);
    }

    private void writeHeader(StreamResult streamResult) throws IOException {
        // Forces a newline after the header to make the output more similar to the sample file
        // See https://stackoverflow.com/a/44323508
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        Document header = documentBuilder.newDocument();
        header.setXmlStandalone(true); // removes standalone="no" XML key
        try {
            transformer.transform(new DOMSource(header), streamResult);
        } catch (TransformerException e) {
            throw new IOException(e);
        }
    }

    private void writeDoc(Document doc, StreamResult streamResult) throws IOException {
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        try {
            transformer.transform(new DOMSource(doc), streamResult);
        } catch (TransformerException e) {
            throw new IOException(e);
        }
    }
}
