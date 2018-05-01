package at.medunigraz.imi.bst.n2c2;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.RuleBasedClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.BasicEvaluator;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.evaluator.OfficialEvaluator;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.metrics.Metrics;
import at.medunigraz.imi.bst.n2c2.stats.CSVStatsWriter;
import at.medunigraz.imi.bst.n2c2.stats.StatsWriter;
import at.medunigraz.imi.bst.n2c2.stats.XMLStatsWriter;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import at.medunigraz.imi.bst.n2c2.validation.CrossValidator;
import at.medunigraz.imi.bst.n2c2.validation.SingleFoldValidator;
import at.medunigraz.imi.bst.n2c2.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RuleBasedClassifierRunner {

    private static final Logger LOG = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        final File dataFolder = new File("data");
        final File xmlStatsFile = new File("stats/rbc.xml");
        final File csvStatsFile = new File("stats/rbc.csv");

        List<Patient> patients = DatasetUtil.loadFromFolder(dataFolder);
        ClassifierFactory factory = new RuleBasedClassifierFactory();

        Evaluator officialEvaluator = new OfficialEvaluator();
        Evaluator basicEvaluator = new BasicEvaluator();

        Validator sfv = new SingleFoldValidator(patients, factory, basicEvaluator);
        Validator cv = new CrossValidator(patients, factory, basicEvaluator);

        Metrics metrics = cv.validate();

        StatsWriter xmlWriter = new XMLStatsWriter(xmlStatsFile);
        xmlWriter.write(metrics);
        xmlWriter.close();

        StatsWriter csvWriter = new CSVStatsWriter(csvStatsFile);
        csvWriter.write(metrics);
        csvWriter.close();
    }
}
