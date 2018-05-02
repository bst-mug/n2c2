package at.medunigraz.imi.bst.n2c2.rules.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class MakesDecisions extends BaseClassifiable {

	private static final List<Pattern> NEGATIVE_MARKERS = new ArrayList<>();

    static {
    	
        NEGATIVE_MARKERS.add(Pattern.compile("", Pattern.CASE_INSENSITIVE)); // .xml: 
        
    }
	
	
    @Override
    public Eligibility isMet(Patient p) {
//        return Eligibility.MET;
    	return findAnyPattern(p.getText(), NEGATIVE_MARKERS) ? Eligibility.NOT_MET : Eligibility.MET;
    }
}