package at.medunigraz.imi.bst.n2c2.classifier.rules;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.rules.RuleBasedClassifier;
import at.medunigraz.imi.bst.n2c2.rules.Rules;

public class RuleBasedClassifierTest {

	@Test 
	public void get_HbA1c_Criterion() throws IOException, SAXException{
		
		File sample = new File(getClass().getResource("/gold-standard/sample.xml").getPath());
		Patient pat = new PatientDAO().fromXML(sample);
		
		RuleBasedClassifier rbc = new RuleBasedClassifier(); 
		
		Rules r = new Rules(); 
		
//		
//		String[] criterion_hba1c = r.a_CriterionID_hba1c; 
//		Boolean is_met = rbc.is_criterion_met(pat, criterion_hba1c, r.getRegex_hba1c());
//		assertTrue(is_met == null); 
//		
		
		
		
		
		
		
	} // End of get_HbA1c_Criterion() 
	
	
	
}
