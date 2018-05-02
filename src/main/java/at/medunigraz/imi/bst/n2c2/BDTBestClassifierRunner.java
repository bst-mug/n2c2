package at.medunigraz.imi.bst.n2c2;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.medunigraz.imi.bst.n2c2.classifier.factory.BDTClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.OfficialEvaluator;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.metrics.MetricSet;
import at.medunigraz.imi.bst.n2c2.stats.CSVStatsWriter;
import at.medunigraz.imi.bst.n2c2.stats.StatsWriter;
import at.medunigraz.imi.bst.n2c2.stats.XMLStatsWriter;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import at.medunigraz.imi.bst.n2c2.validation.CrossValidator;

/**
 * Crossvalidation of BILSTMC3G with boosted (AdaBoostM1) decision tree
 * (RandomForest). Crossvalidation of boosted (AdaBoostM1) decision tree
 * (RandomForest) is made on full BILSTMC3G output.
 * 
 * @author Markus
 *
 */
public class BDTBestClassifierRunner {

	private static final Logger LOG = LogManager.getLogger();

	public static void main(String[] args) throws IOException {
		final File dataFolder = new File("Z:/n2c2/data/models/samplesTrainingBDT");
		final File xmlStatsFile = new File("stats/best.xml");
		final File csvStatsFile = new File("stats/best.csv");

		// set port for monitoring neural networks
		Properties props = System.getProperties();
		props.setProperty("org.deeplearning4j.ui.port", "9001");

		List<Patient> patients = DatasetUtil.loadFromFolder(dataFolder);
		ClassifierFactory factory = new BDTClassifierFactory();
		OfficialEvaluator evaluator = new OfficialEvaluator();

		CrossValidator cv = new CrossValidator(patients, factory, evaluator);
		MetricSet metrics = (MetricSet) cv.validate();
		LOG.info(metrics);

		StatsWriter xmlWriter = new XMLStatsWriter(xmlStatsFile);
		xmlWriter.write(metrics);
		xmlWriter.close();

		StatsWriter csvWriter = new CSVStatsWriter(csvStatsFile);
		csvWriter.write(metrics);
		csvWriter.close();
	}
}
