package at.medunigraz.imi.bst.n2c2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.nn.BILSTMClassifier;

public class BILSTMClassifierRunner {

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
		List<File> sampleFiles = (List<File>) FileUtils.listFiles(sampleDirectory, TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);

		try {
			List<Patient> patients = new ArrayList<Patient>();
			for (File patientSample : sampleFiles) {
				patients.add(new PatientDAO().fromXML(patientSample));
			}
			BILSTMClassifier classifier = new BILSTMClassifier(patients, wordVectorData);
			classifier.train(patients);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		LOG.info("Finished training");
	}
}
