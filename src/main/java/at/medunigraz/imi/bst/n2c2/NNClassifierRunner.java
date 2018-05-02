package at.medunigraz.imi.bst.n2c2;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

/**
 * Use for NN (BILSTMC3G) single fold validation.
 * Model is saved.
 * 
 * @author Markus
 *
 */
public class NNClassifierRunner {

	private static final Logger LOG = LogManager.getLogger();

	public static void main(String[] args) throws IOException {
		final File dataFolder = new File("C:/DataN2c2/samplesTraining");
		final File statsFile = new File("stats/best.xml");

		// set port for monitoring neural networks
		Properties props = System.getProperties();
		props.setProperty("org.deeplearning4j.ui.port", "9001");

		List<Patient> patients = DatasetUtil.loadFromFolder(dataFolder);
		ClassifierFactory factory = new NNClassifierFactory();
		Evaluator evaluator = new OfficialEvaluator();

		SingleFoldValidator sfv = new SingleFoldValidator(patients, factory, evaluator);
		MetricSet metrics = (MetricSet) sfv.validate(0.9, 0, 0.1);
		LOG.info(metrics);

		// Writes stats into a CSV file
		StatsWriter writer = new CSVStatsWriter(statsFile);
		writer.write(metrics);
		writer.close();
	}
}
