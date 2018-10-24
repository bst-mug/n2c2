package at.medunigraz.imi.bst.n2c2;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.RuleBasedClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.BasicEvaluator;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.metrics.Metrics;
import at.medunigraz.imi.bst.n2c2.stats.CSVStatsWriter;
import at.medunigraz.imi.bst.n2c2.stats.StatsWriter;
import at.medunigraz.imi.bst.n2c2.stats.XMLStatsWriter;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import at.medunigraz.imi.bst.n2c2.validation.TestValidator;
import at.medunigraz.imi.bst.n2c2.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class RuleBasedClassifierRunner {

    private static final Logger LOG = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        final File trainFolder = new File("data/train");
        final File testFolder = new File("data/test");
        final File xmlStatsFile = new File("stats/rbc.xml");
        final File csvStatsFile = new File("stats/rbc.csv");

        List<Patient> trainPatients = DatasetUtil.loadFromFolder(trainFolder);
        List<Patient> testPatients = DatasetUtil.loadFromFolder(testFolder);
        ClassifierFactory factory = new RuleBasedClassifierFactory();

//        Evaluator evaluator = new OfficialEvaluator();
        Evaluator evaluator = new BasicEvaluator();

//        Validator validator = new SingleFoldValidator(trainPatients, factory, evaluator);
//        Validator validator = new CrossValidator(trainPatients, factory, evaluator);
        Validator validator = new TestValidator(trainPatients, testPatients, factory, evaluator);

        Metrics metrics = validator.validate();

        StatsWriter xmlWriter = new XMLStatsWriter(xmlStatsFile);
        xmlWriter.write(metrics);
        xmlWriter.close();

        StatsWriter csvWriter = new CSVStatsWriter(csvStatsFile);
        csvWriter.write(metrics);
        csvWriter.close();
    }
}
