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
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class DataUtilitiesTest {

	@Ignore
	public void train() {

		// read in patients
		File sampleDirectory = new File("Z:/n2c2/data/samplesTraining");
		List<File> sampleFiles = (List<File>) FileUtils.listFiles(sampleDirectory, TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);

		DataUtilities utilities = new DataUtilities();

		List<Patient> patients;
		List<String> sentences;
		try {
			patients = new ArrayList<Patient>();
			for (File patientSample : sampleFiles) {
				patients.add(new PatientDAO().fromXML(patientSample));
			}

			System.out.println(patients.get(200).getID());
			sentences = DataUtilities.getSentences(patients.get(200).getText());

			for (String sentence : sentences) {
				String normalized = utilities.processTextReduced(sentence);
				String char3Grams001 = utilities.getChar3GramRepresentation(normalized);
				System.out.println(sentence + "\t" + normalized + "\t" + char3Grams001);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		assertEquals(true, true);
	}
}
