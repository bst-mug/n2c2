package at.medunigraz.imi.bst.n2c2.preprocess.conceptmapper;

import bioc.BioCDocument;
import gov.nih.nlm.nls.metamap.document.FreeText;
import gov.nih.nlm.nls.metamap.lite.types.ConceptInfo;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import gov.nih.nlm.nls.ner.MetaMapLite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Facade for MetaMapLite (https://metamap.nlm.nih.gov/MetaMapLite.shtml).
 * Requires an UMLS license.
 *
 * @author Michel Oleynik <michel.oleynik@stud.medunigraz.at>
 * @link https://github.com/michelole/reassess/blob/master/src/main/java/at/medunigraz/imi/reassess/conceptmapper/metamap/MetaMapLiteFacade.java
 */
public class MetaMapLiteFacade implements ConceptMapper {

    private static final Logger LOG = LogManager.getLogger();

    private static MetaMapLiteFacade instance = null;
    private static Properties properties;
    private MetaMapLite metaMapLiteInst;

    private MetaMapLiteFacade() {
        LOG.info("Building MetaMap instance...");

        initProperties();

        try {
            metaMapLiteInst = new MetaMapLite(properties);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        LOG.info("Building MetaMap instance finished.");
    }

    public static MetaMapLiteFacade getInstance() {
        if (instance == null) {
            instance = new MetaMapLiteFacade();
        }
        return instance;
    }

    private static void initProperties() {
        properties = MetaMapLite.getDefaultConfiguration();

        String configPropertyFilename = System.getProperty("metamaplite.property.file",
                MetaMapLiteFacade.class.getResource("/metamaplite.properties").getFile());

        try {
            properties.load(new FileReader(configPropertyFilename));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        MetaMapLite.expandModelsDir(properties);
        MetaMapLite.expandIndexDir(properties);
    }

    public static boolean isModelsDirValid() {
        initProperties();
        return (new File(properties.getProperty("opennlp.models.directory"))).canRead();
    }

    /*
     * (non-Javadoc)
     * @see at.medunigraz.imi.reassess.conceptmapper.ConceptMapper#map(java.lang.String)
     */
    public List<String> map(String text) {
        List<String> ret = new ArrayList<String>();

        List<Entity> entityList = process(text);

        for (Entity entity : entityList) {
            // TODO Should submatches be skipped as in annotate()?
            for (Ev ev : entity.getEvSet()) {
                ret.add(ev.getConceptInfo().getCUI());
                LOG.trace(ev);
            }
        }

        return ret;
    }

    private List<Entity> process(String text) {
        int length = text.length();
        LOG.debug("Processing \"{}\"...", text.substring(0, Math.min(length, 20)));

        long start = System.currentTimeMillis();

        BioCDocument document = FreeText.instantiateBioCDocument(text);
        document.setID("1");
        List<BioCDocument> documentList = new ArrayList<BioCDocument>();
        documentList.add(document);

        List<Entity> entityList = null;
        try {
            entityList = metaMapLiteInst.processDocumentList(documentList);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        float duration = (end - start + 1) / 1000f;

        LOG.debug("Processed {} chars in {} sec ({} chars/sec).", length, duration, length / duration);

        return entityList;
    }

    /*
     * (non-Javadoc)
     * @see at.medunigraz.imi.reassess.conceptmapper.ConceptMapper#annotate(java.lang.String)
     */
    public String annotate(String text) {
        List<Entity> entityList = process(text);

        int length = text.length();

        StringBuilder sb = new StringBuilder(length);

        int i = 0;
        for (Entity entity : entityList) {
            int start = entity.getStart();

            // Skip submatches
            if (start < i) {
                continue;
            }

            String matched = entity.getMatchedText();

            sb.append(text, i, start);
            sb.append("<");
            sb.append(matched);
            sb.append("|");

            for (Ev ev : entity.getEvSet()) {
                ConceptInfo conceptInfo = ev.getConceptInfo();
                sb.append(conceptInfo.getCUI());
                sb.append(":");
                sb.append(conceptInfo.getPreferredName());
                sb.append("|");
            }
            sb.append(">");

            i = entity.getStart() + entity.getLength();
        }

        sb.append(text, i, length);

        return sb.toString();
    }

}