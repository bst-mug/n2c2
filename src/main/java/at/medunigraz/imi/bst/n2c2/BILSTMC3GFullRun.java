package at.medunigraz.imi.bst.n2c2;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import at.medunigraz.imi.bst.n2c2.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.nn.BILSTMC3GClassifier;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;

/**
 * Full run of BILSTMC3G.
 * 
 * @author Markus
 *
 */
public class BILSTMC3GFullRun {

	private static final Logger LOG = LogManager.getLogger();

	public static void main(String[] args) throws IOException {

		// path to persisted BILSTMC3G model
		final String modelPath = Config.NN_MODELS;

		// path for saving prediction on test patients
		final String pathPredicted = Config.NN_SAMPLES_PREDICTED_BDT;

		// real test data
		List<Patient> patientsTestBILSTMC3G = DatasetUtil.loadFromFolder(new File("data/test"));

		// set port for monitoring neural networks
		Properties props = System.getProperties();
		props.setProperty("org.deeplearning4j.ui.port", "9001");

		// generate BILSTMC3G output
		BILSTMC3GClassifier biLSTMC3GClassifier = new BILSTMC3GClassifier(modelPath);
		List<Patient> predicted = biLSTMC3GClassifier.predict(patientsTestBILSTMC3G);

		// write out
		for (Patient p : predicted) {
			new PatientDAO().toXML(p, new File(pathPredicted + p.getID()));
		}
	}
}
