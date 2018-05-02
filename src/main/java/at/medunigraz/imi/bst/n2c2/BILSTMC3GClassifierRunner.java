package at.medunigraz.imi.bst.n2c2;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.nn.BILSTMC3GClassifier;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;

/**
 * Train BILSTMC3G data until 0.95 training accuray.
 * 
 * @author Markus
 *
 */
public class BILSTMC3GClassifierRunner {

	private static final Logger LOG = LogManager.getLogger();

	public static void main(String[] args) {

		String patientData = "";

		// check arguments
		if (args.length < 1) {
			System.out.println("Missing input information for files");
			System.exit(0);
		} else {
			patientData = args[0];
		}

		// set port for monitoring
		Properties props = System.getProperties();
		props.setProperty("org.deeplearning4j.ui.port", "9001");

		// read in patients
		File sampleDirectory = new File(patientData);
		List<Patient> patients = DatasetUtil.loadFromFolder(sampleDirectory);

		BILSTMC3GClassifier classifier = new BILSTMC3GClassifier();
		classifier.train(patients);

		LOG.info("Finished training");
	}
}
