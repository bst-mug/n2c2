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
    public static Patient fromXML(File xmlFile) throws IOException, SAXException {
        return fromXML(new FileInputStream(xmlFile));
    }

    public static Patient fromXML(InputStream xml) throws IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Document doc = documentBuilder.parse(xml);

        String text = doc.getElementsByTagName("TEXT").item(0).getTextContent();
        Patient patient = new Patient().withText(text);

        Element tagsElement = (Element) doc.getElementsByTagName("TAGS").item(0);

        NodeList tags = tagsElement.getElementsByTagName("*");
        for (int i = 0; i < tags.getLength(); i++) {
            Element element = (Element) tags.item(i);
            XMLTag tag = XMLTag.fromElement(element);
            patient.withCriterion(tag.getCriterion(), tag.getEligibility());
        }

        return patient;
    }

    public static void toXML(Patient patient, File outputFile) throws IOException {
        toXML(patient, new FileOutputStream(outputFile));
    }

    public static OutputStream toXML(Patient patient, OutputStream outputStream) throws IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Document doc = documentBuilder.newDocument();

        Element rootElement = doc.createElement("PatientMatching");
        doc.appendChild(rootElement);

        Element textElement = doc.createElement("TEXT");
        textElement.appendChild(doc.createCDATASection(patient.getText()));
        rootElement.appendChild(textElement);

        Element tagsElement = doc.createElement("TAGS");
        rootElement.appendChild(tagsElement);

        for (Criterion c : Criterion.values()) {
            Eligibility e = patient.getEligibility(c);
            if (e == null) {
                continue;
            }

            Element criterionElement = doc.createElement(c.toString());
            criterionElement.setAttribute("met", e.toString());
            tagsElement.appendChild(criterionElement);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(outputStream);

        // Forces a newline after the header to make the output more similar to the sample file
        // See https://stackoverflow.com/a/44323508
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        Document header = documentBuilder.newDocument();
        header.setXmlStandalone(true); // removes standalone="no" XML key
        try {
            transformer.transform(new DOMSource(header), result);
        } catch (TransformerException e) {
            throw new IOException(e);
        }

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        try {
            transformer.transform(new DOMSource(doc), result);
        } catch (TransformerException e) {
            throw new IOException(e);
        }

        return null;
    }
}
