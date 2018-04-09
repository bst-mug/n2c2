package at.medunigraz.imi.bst.n2c2.zoning;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.time.Period;
import java.util.ArrayList;

import org.junit.Test;
import org.xml.sax.SAXException;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.PatientVisits;

public class ZoningTest {

	@Test 
	public void getAllData() throws IOException, SAXException{
		
		File sample = new File(getClass().getResource("/gold-standard/sample.xml").getPath());
		Patient pat = new PatientDAO().fromXML(sample);
		
		ArrayList<PatientVisits> a_pv = pat.getAllVisits(); 
		int size = a_pv.size(); 
		assertTrue(size == 3); 
		
		ArrayList<PatientVisits> a_multipv = pat.getMultipleVisits(12); 
		int sizeMulti = a_multipv.size(); 
		assertTrue(sizeMulti == 2); 
		
//		PatientVisits pv0 = a_pv.get(0);
//		assertNull(pv0.getVisit_text());
//		assertNull(pv0.getVisit_date());
		
		PatientVisits pv0 = a_pv.get(0);
		assertThat(pv0.getVisit_text(), containsString("FISHKILL"));
		
		PatientVisits pv1 = a_pv.get(1);
		assertThat(pv1.getVisit_text(), containsString("HPI"));
		
		PatientVisits pv2 = a_pv.get(2);
		assertThat(pv2.getVisit_text(), containsString("PHYSICAL EXAMINATION"));
		
		PatientVisits tpv_first = pat.getFirstVisit(); 
		assertThat(tpv_first.getVisit_text(), containsString("FISHKILL"));
		
		PatientVisits tpv_last = pat.getLastVisit(); 
		assertThat(tpv_last.getVisit_text(), containsString("PHYSICAL EXAMINATION"));		
		
		Period p = pat.getTimeIntervalBetweenVisits(tpv_first, tpv_last); 
		// first visit: 2067-05-22 ... last visit: 2069-11-02
		int p_days = p.getDays(); 
		int p_months = p.getMonths(); 
		int p_years = p.getYears(); 
		
		assertTrue(p_days == 11); 
		assertTrue(p_months == 5); 
		assertTrue(p_years == 2); 
		
		
		
	}
	
	
}
