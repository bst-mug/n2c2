package at.medunigraz.imi.bst.n2c2;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.SVMClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.OfficialEvaluator;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.metrics.OfficialMetrics;
import at.medunigraz.imi.bst.n2c2.stats.StatsWriter;
import at.medunigraz.imi.bst.n2c2.stats.XMLStatsWriter;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import at.medunigraz.imi.bst.n2c2.validation.CrossValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BestClassifierRunner {

    private static final Logger LOG = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        final File dataFolder = new File("data");
        final File statsFile = new File("stats/best.xml");

        List<Patient> patients = DatasetUtil.loadFromFolder(dataFolder);
        ClassifierFactory factory = new SVMClassifierFactory();
        OfficialEvaluator evaluator = new OfficialEvaluator();

        CrossValidator cv = new CrossValidator(patients, factory, evaluator);
        OfficialMetrics metrics = (OfficialMetrics) cv.evaluate();
        LOG.info(metrics);

        StatsWriter writer = new XMLStatsWriter(statsFile);
        writer.write(metrics);
        writer.close();
    }
}
