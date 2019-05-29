package at.medunigraz.imi.bst.n2c2.nn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.medunigraz.imi.bst.n2c2.nn.iterator.NGramIterator;
import org.junit.Test;
import org.xml.sax.SAXException;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class BILSTMC3GClassifierTest extends BaseNNClassifierTest {

	public BILSTMC3GClassifierTest() {
		this.classifier = new BILSTMC3GClassifier();
	}

	@Test
	public void saveAndLoad() throws IOException, SAXException {
		Patient p = new PatientDAO().fromXML(SAMPLE);

		List<Patient> train = new ArrayList<>();
		train.add(p);

		// We first train on some examples...
		BILSTMC3GClassifier trainClassifier = new BILSTMC3GClassifier();
		trainClassifier.deleteModelDir(train);	// Delete any previously trained models, to ensure training is tested
		trainClassifier.train(train);			// This should persist models
		assertTrue(trainClassifier.isTrained(train));

		// ... and then try to load the model on a new instance.
		BILSTMC3GClassifier testClassifier = new BILSTMC3GClassifier();
		assertTrue(testClassifier.isTrained(train));
		// TODO use Mockito to call train() and ensure trainFullSetBMC is NOT called.
		testClassifier.initializeNetworkFromFile(BaseNNClassifier.getModelPath(train));

		// Check classifier attributes are properly initialized
		assertEquals(trainClassifier.truncateLength, testClassifier.truncateLength);
		assertEquals(trainClassifier.vectorSize, testClassifier.vectorSize);

		// Check maps are properly initialized
		NGramIterator trainIterator = (NGramIterator)trainClassifier.fullSetIterator;
		NGramIterator testIterator = (NGramIterator)testClassifier.fullSetIterator;
		assertEquals(trainIterator.characterNGram_3, testIterator.characterNGram_3);
		assertEquals(trainIterator.char3GramToIdxMap, testIterator.char3GramToIdxMap);

		// XXX maxSentences is not initialized, but is not needed for prediction
		//assertEquals(trainIterator.maxSentences, testIterator.maxSentences);

		assertSamplePatient(testClassifier, p);
	}
}
