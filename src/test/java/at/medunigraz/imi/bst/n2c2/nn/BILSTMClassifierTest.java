package at.medunigraz.imi.bst.n2c2.nn;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Ignore;
import org.xml.sax.SAXException;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class BILSTMClassifierTest {

	@Ignore
	public void train() {

		// read in patients
		File sampleDirectory = new File("Z:/n2c2/data/samplesTraining");
		List<File> sampleFiles = (List<File>) FileUtils.listFiles(sampleDirectory, TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);

		List<Patient> patients;
		try {
			patients = new ArrayList<Patient>();
			for (File patientSample : sampleFiles) {
				patients.add(new PatientDAO().fromXML(patientSample));
			}

			BILSTMClassifier classifier = new BILSTMClassifier(patients);
			classifier.train(patients);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		assertEquals(true, true);
	}

	@Ignore
	public void initializeNetworkFromFile() {

		String modelName = "N2c2BILSTM_MBL_Full_50.zip";
		BILSTMClassifier classifier = new BILSTMClassifier();
		classifier.initializeNetworkFromFile(modelName);

		assertEquals(true, true);
	}

	@Ignore
	public void predictCriterion() {
		// read in patients
		File sampleDirectory = new File("Z:/n2c2/data/samplesTraining");
		List<File> sampleFiles = (List<File>) FileUtils.listFiles(sampleDirectory, TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);

		String modelName = "N2c2BILSTM_MBL_Full_50.zip";
		String pathToWordVectors = "C:\\Users\\Markus\\Downloads\\GoogleNews-vectors-negative300.bin.gz";

		List<Patient> patients;
		try {
			patients = new ArrayList<Patient>();
			for (File patientSample : sampleFiles) {
				patients.add(new PatientDAO().fromXML(patientSample));

			}
			BILSTMClassifier classifier = new BILSTMClassifier(pathToWordVectors, modelName);
			classifier.predict(patients.get(0), Criterion.ABDOMINAL);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		assertEquals(true, true);
	}

	@Ignore
	public void predictPatientList() {
		// read in patients
		File sampleDirectory = new File("Z:/n2c2/data/samplesTraining");
		List<File> sampleFiles = (List<File>) FileUtils.listFiles(sampleDirectory, TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);

		String modelName = "N2c2BILSTM_MBL_Full_50.zip";
		String pathToWordVectors = "C:\\Users\\Markus\\Downloads\\GoogleNews-vectors-negative300.bin.gz";

		List<Patient> patients;
		try {
			patients = new ArrayList<Patient>();
			for (File patientSample : sampleFiles) {
				patients.add(new PatientDAO().fromXML(patientSample));

			}
			BILSTMClassifier classifier = new BILSTMClassifier(pathToWordVectors, modelName);
			classifier.predict(patients);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		assertEquals(true, true);
	}
}
