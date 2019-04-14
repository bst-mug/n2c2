package at.medunigraz.imi.bst.n2c2.nn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import org.junit.Test;
import org.xml.sax.SAXException;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class BILSTMC3GClassifierTest {

	private static final File SAMPLE = new File(BILSTMC3GClassifierTest.class.getResource("/gold-standard/sample.xml").getPath());

	@Test
	public void predictSample() throws IOException, SAXException {
		Patient p = new PatientDAO().fromXML(SAMPLE);

		// For test purposes only, we train and test on the same single patient
		List<Patient> train = new ArrayList<>();
		train.add(p);

		BILSTMC3GClassifier nn = new BILSTMC3GClassifier();
		nn.deleteModelDir(train);	// Delete any previously trained models, to ensure training is tested
		nn.train(train);

		assertSamplePatient(nn, p);
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

	private void assertSamplePatient(BILSTMC3GClassifier nn, Patient p) {
		assertEquals(Eligibility.NOT_MET, nn.predict(p, Criterion.ABDOMINAL));
		assertEquals(Eligibility.MET, nn.predict(p, Criterion.ADVANCED_CAD));
		assertEquals(Eligibility.NOT_MET, nn.predict(p, Criterion.ALCOHOL_ABUSE));
		assertEquals(Eligibility.MET, nn.predict(p, Criterion.ASP_FOR_MI));
		assertEquals(Eligibility.NOT_MET, nn.predict(p, Criterion.CREATININE));
		assertEquals(Eligibility.MET, nn.predict(p, Criterion.DIETSUPP_2MOS));
		assertEquals(Eligibility.NOT_MET, nn.predict(p, Criterion.DRUG_ABUSE));
		assertEquals(Eligibility.MET, nn.predict(p, Criterion.ENGLISH));
		assertEquals(Eligibility.MET, nn.predict(p, Criterion.HBA1C));
		assertEquals(Eligibility.NOT_MET, nn.predict(p, Criterion.KETO_1YR));
		assertEquals(Eligibility.MET, nn.predict(p, Criterion.MAJOR_DIABETES));
		assertEquals(Eligibility.MET, nn.predict(p, Criterion.MAKES_DECISIONS));
		assertEquals(Eligibility.MET, nn.predict(p, Criterion.MI_6MOS));
	}

}
