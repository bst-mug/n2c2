package at.medunigraz.imi.bst.n2c2;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.nn.CNNClassifier;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;

public class CNNClassifierRunner {

	private static final Logger LOG = LogManager.getLogger();

	public static void main(String[] args) {

		String patientData = "";
		String wordVectorData = "";

		// check arguments
		if (args.length < 2) {
			System.out.println("Mssing input information wordvectors or processing files");
			System.exit(0);
		} else {
			patientData = args[0];
			wordVectorData = args[1];
		}

		// set port for monitoring
		Properties props = System.getProperties();
		props.setProperty("org.deeplearning4j.ui.port", "9001");

		// read in patients
		File sampleDirectory = new File(patientData);
		List<Patient> patients = DatasetUtil.loadFromFolder(sampleDirectory);

		CNNClassifier classifier = new CNNClassifier(patients, wordVectorData);
		// classifier.train(patients);

		LOG.info("Finished training");
	}
}
