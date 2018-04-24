package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class HbA1c implements Classifiable {

	@Override
	public  Eligibility is_met(Patient p) {
		return Eligibility.NOT_MET;
		// TODO Auto-generated method stub
		
	}

	
	
}
