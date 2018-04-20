package at.medunigraz.imi.bst.n2c2;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.NNClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.evaluator.OfficialEvaluator;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.metrics.MetricSet;
import at.medunigraz.imi.bst.n2c2.stats.CSVStatsWriter;
import at.medunigraz.imi.bst.n2c2.stats.StatsWriter;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import at.medunigraz.imi.bst.n2c2.validation.SingleFoldValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class NNClassifierRunner {

    private static final Logger LOG = LogManager.getLogger();

    public static void main(String[] args) throws IOException {
        final File dataFolder = new File("data");
        final File statsFile = new File("stats/best.xml");

        List<Patient> patients = DatasetUtil.loadFromFolder(dataFolder);
        ClassifierFactory factory = new NNClassifierFactory();
        Evaluator evaluator = new OfficialEvaluator();

        SingleFoldValidator sfv = new SingleFoldValidator(patients, factory, evaluator);
        MetricSet metrics = (MetricSet) sfv.validate();
        LOG.info(metrics);

        // Writes stats into a CSV file
        StatsWriter writer = new CSVStatsWriter(statsFile);
        writer.write(metrics);
        writer.close();
    }
}
