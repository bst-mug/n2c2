package at.medunigraz.imi.bst.n2c2.rules.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class DrugAbuse extends BaseClassifiable {

	private static final List<Pattern> POSITIVE_MARKERS = new ArrayList<>();

    static {
    	
    	POSITIVE_MARKERS.add(Pattern.compile("cocaine", Pattern.CASE_INSENSITIVE)); // 356.xml: he has used cocaine and crack
    	
//    	POSITIVE_MARKERS.add(Pattern.compile("marijuana", Pattern.CASE_INSENSITIVE)); // 382.xml: +occasional marijuana.
    
    	POSITIVE_MARKERS.add(Pattern.compile("IV drug user", Pattern.CASE_INSENSITIVE)); // 382.xml: H/O drug abuse : IV drug user stopped heroin
    	
//    	POSITIVE_MARKERS.add(Pattern.compile("substance abuse", Pattern.CASE_INSENSITIVE)); // 159.xml: history of noncompliance and substance abuse.
    	
    	
    }
	
	
	
    @Override
    public Eligibility isMet(Patient p) {
//        return Eligibility.NOT_MET;
    	return findAnyPattern(p.getCleanedText(), POSITIVE_MARKERS) ? Eligibility.MET : Eligibility.NOT_MET;
    }
}