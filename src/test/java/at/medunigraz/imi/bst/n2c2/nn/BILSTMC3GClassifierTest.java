package at.medunigraz.imi.bst.n2c2.nn;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;

public class BILSTMC3GClassifierTest {

	@Test
	public void predictSample() throws IOException, SAXException {
		final File SAMPLE = new File(getClass().getResource("/gold-standard/sample.xml").getPath());
		Patient p = new PatientDAO().fromXML(SAMPLE);

		// For test purposes only, we train and test on the same single patient
		List<Patient> train = new ArrayList<>();
		train.add(p);

		BILSTMC3GClassifier nn = new BILSTMC3GClassifier();
		nn.deleteModelDir(train);	// Delete any previously trained models, to ensure training is tested
		nn.train(train);

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
