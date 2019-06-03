package at.medunigraz.imi.bst.n2c2.nn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.xml.sax.SAXException;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class BiLSTMCharacterTrigramClassifierTest extends BaseNNClassifierTest {

	public BiLSTMCharacterTrigramClassifierTest() {
		this.classifier = new BiLSTMCharacterTrigramClassifier();
	}

	@Test
	public void saveAndLoad() throws IOException, SAXException {
		Patient p = new PatientDAO().fromXML(SAMPLE);

		List<Patient> train = new ArrayList<>();
		train.add(p);

		// We first train on some examples...
		BiLSTMCharacterTrigramClassifier trainClassifier = new BiLSTMCharacterTrigramClassifier();
		trainClassifier.deleteModelDir(train);	// Delete any previously trained models, to ensure training is tested
		trainClassifier.train(train);			// This should persist models
		assertTrue(trainClassifier.isTrained(train));

		// ... and then try to load the model on a new instance.
		BiLSTMCharacterTrigramClassifier testClassifier = new BiLSTMCharacterTrigramClassifier();
		assertTrue(testClassifier.isTrained(train));
		// TODO use Mockito to call train() and ensure trainFullSetBMC is NOT called.
		testClassifier.initializeNetworkFromFile(BaseNNClassifier.getModelPath(train));

		assertSamplePatient(testClassifier, p);
	}
}
