package at.medunigraz.imi.bst.n2c2;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.FactoryProvider;
import at.medunigraz.imi.bst.n2c2.evaluator.BasicEvaluator;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.evaluator.OfficialEvaluator;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.metrics.Metrics;
import at.medunigraz.imi.bst.n2c2.stats.CSVStatsWriter;
import at.medunigraz.imi.bst.n2c2.stats.StatsWriter;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import at.medunigraz.imi.bst.n2c2.validation.TestValidator;
import at.medunigraz.imi.bst.n2c2.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ClassifierRunner {

    private static final Logger LOG = LogManager.getLogger();

    private static final ClassifierFactory[] CLASSIFIERS = new ClassifierFactory[] {
            FactoryProvider.getMajorityFactory(),
            FactoryProvider.getRBCFactory(),
            FactoryProvider.getSVMFactory(),
            FactoryProvider.getSelfTrainedPerceptronFactory(),
            FactoryProvider.getPreTrainedPerceptronFactory(),
            FactoryProvider.getLSTMSelfTrainedFactory(),
            FactoryProvider.getLSTMPreTrainedFactory()
    };

    public static void main(String[] args) throws IOException {
        final File trainFolder = new File("data/train");
        final File testFolder = new File("data/test");
        final File statsFolder = new File("stats");

        List<Patient> trainPatients = DatasetUtil.loadFromFolder(trainFolder);
        List<Patient> testPatients = DatasetUtil.loadFromFolder(testFolder);
        Collections.sort(testPatients);

        Evaluator officialEvaluator = new OfficialEvaluator();	// n2c2 official metrics
        Evaluator basicEvaluator = new BasicEvaluator();		// accuracy and fp/fn metrics

        for (ClassifierFactory factory : CLASSIFIERS) {
            String name = factory.toString();
            LOG.info("Running {}...", name);

            Validator officialValidator = new TestValidator(trainPatients, testPatients, factory, officialEvaluator);
            Validator basicValidator = new TestValidator(trainPatients, testPatients, factory, basicEvaluator);

            Metrics officialMetrics = officialValidator.validate();
            Metrics basicMetrics = basicValidator.validate();

            writeAndClose(officialMetrics, new File(statsFolder, name + "-official.csv"));
            writeAndClose(basicMetrics, new File(statsFolder, name + "-basic.csv"));
        }
    }

    private static void writeAndClose(Metrics metrics, File file) throws IOException {
        StatsWriter csvWriter = new CSVStatsWriter(file);
        csvWriter.write(metrics);
        csvWriter.close();
    }
}
