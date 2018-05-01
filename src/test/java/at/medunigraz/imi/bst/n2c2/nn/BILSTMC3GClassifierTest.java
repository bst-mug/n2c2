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
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;

public class BILSTMC3GClassifierTest {

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

			BILSTMC3GClassifier classifier = new BILSTMC3GClassifier();
			classifier.train(patients);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		assertEquals(true, true);
	}

	@Ignore
	public void predictAndOverwrite() {
		// read in patients
		File sampleDirectory = new File("Z:/n2c2/data/samplesTraining");
		List<File> sampleFiles = (List<File>) FileUtils.listFiles(sampleDirectory, TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);

		String modelPath = "Z:/n2c2/data/models/";
		String pathTrainingBDT = "Z:/n2c2/data/samplesTrainingBDT/";

		List<Patient> patients;
		try {
			patients = new ArrayList<Patient>();
			for (File patientSample : sampleFiles) {
				patients.add(new PatientDAO().fromXML(patientSample));

			}
			BILSTMC3GClassifier classifier = new BILSTMC3GClassifier(modelPath);
			patients.forEach(p -> classifier.predictAndOverwrite(p, pathTrainingBDT));

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		assertEquals(true, true);
	}

	@Ignore
	public void predictCriterion() {
		// read in patients
		File sampleDirectory = new File("Z:/n2c2/data/samplesTraining");
		List<File> sampleFiles = (List<File>) FileUtils.listFiles(sampleDirectory, TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);

		String modelPath = "Z:/n2c2/data/models/";

		List<Patient> patients;
		try {
			patients = new ArrayList<Patient>();
			for (File patientSample : sampleFiles) {
				patients.add(new PatientDAO().fromXML(patientSample));

			}
			BILSTMC3GClassifier classifier = new BILSTMC3GClassifier(modelPath);
			classifier.predict(patients.get(0), Criterion.ABDOMINAL);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		assertEquals(true, true);
	}

	@Ignore
	public void predictPatients() {
		// read in patients
		File sampleDirectory = new File("Z:/n2c2/data/samplesTraining");
		List<File> sampleFiles = (List<File>) FileUtils.listFiles(sampleDirectory, TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);

		String modelPath = "Z:/n2c2/data/models/";

		List<Patient> patients;
		try {
			patients = new ArrayList<Patient>();
			for (File patientSample : sampleFiles) {
				patients.add(new PatientDAO().fromXML(patientSample));

			}
			BILSTMC3GClassifier classifier = new BILSTMC3GClassifier(modelPath);
			classifier.predict(DatasetUtil.stripTags(patients));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		assertEquals(true, true);
	}
}
