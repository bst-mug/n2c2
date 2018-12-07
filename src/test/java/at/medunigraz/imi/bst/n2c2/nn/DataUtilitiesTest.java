package at.medunigraz.imi.bst.n2c2.nn;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class DataUtilitiesTest {

	private static final DataUtilities UTILITIES = new DataUtilities();

	@Test
	public void processTextReduced() throws IOException {
		String normalized = UTILITIES.processTextReduced("This is a, test    sentence: test_sentence.");
		assertEquals("this is a test sentenc test sent", normalized);
	}

	@Test
	public void getChar3GramRepresentation() throws IOException {
		String normalized = UTILITIES.getChar3GramRepresentation("this is a test sentence");
		assertEquals("_th thi his is_ _is is_ _a_ _te tes est st_ _se sen ent nte ten enc nce ce_", normalized);
	}

	@Test
	public void sample() throws IOException, SAXException {
		final File SAMPLE = new File(getClass().getResource("/gold-standard/sample.xml").getPath());
		Patient p = new PatientDAO().fromXML(SAMPLE);

		StringBuilder normalizedText = new StringBuilder();
		StringBuilder textTrigrams = new StringBuilder();

		List<String> sentences = DataUtilities.getSentences(p.getText());
		for (String sentence : sentences) {
			String normalized = UTILITIES.processTextReduced(sentence);
			String charTrigrams = UTILITIES.getChar3GramRepresentation(normalized);

			normalizedText.append(normalized);
			normalizedText.append("\n");

			textTrigrams.append(charTrigrams);
			textTrigrams.append("\n");
		}

		final File expectedNormalized = new File(getClass().getResource("/nn/sample-normalized.txt").getFile());
		final File expectedTrigrams = new File(getClass().getResource("/nn/sample-trigrams.txt").getFile());

		assertEquals(FileUtils.readFileToString(expectedNormalized, "UTF-8"), normalizedText.toString());
		assertEquals(FileUtils.readFileToString(expectedTrigrams, "UTF-8"), textTrigrams.toString());
	}

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
