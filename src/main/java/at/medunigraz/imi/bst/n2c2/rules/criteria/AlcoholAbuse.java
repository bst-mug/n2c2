package at.medunigraz.imi.bst.n2c2.rules.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class AlcoholAbuse extends BaseClassifiable {

	private static final List<Pattern> POSITIVE_MARKERS = new ArrayList<>();

//	private static final List<Pattern> NEGATIVE_MARKERS = new ArrayList<>();
	
	
    static {
    	
    	POSITIVE_MARKERS.add(Pattern.compile("(Hx|History) of alcoholism", Pattern.CASE_INSENSITIVE)); // 176.xml: have resulted in failure because of his Hx of alcoholism  
    	
    	// TODO -- grouping of drinks or beers per day 
    	
    	POSITIVE_MARKERS.add(Pattern.compile("(beer|beers|drinks) per day", Pattern.CASE_INSENSITIVE)); // 159.xml: she drinks about one beer per day 
    	
    	POSITIVE_MARKERS.add(Pattern.compile("beers/night", Pattern.CASE_INSENSITIVE)); // 325.xml: 5-6 beers/night on wkends
    	
    	POSITIVE_MARKERS.add(Pattern.compile("binge\\p{javaWhitespace}{1,20}drinker", Pattern.CASE_INSENSITIVE)); // 187.xml: depressed he becomes a binge drinker
    	
    	POSITIVE_MARKERS.add(Pattern.compile("amount of alcohol", Pattern.CASE_INSENSITIVE)); // 258.xml: wife is concerned about the amount of alcohol he was drinking
    	
//    	POSITIVE_MARKERS.add(Pattern.compile("heavy drinking", Pattern.CASE_INSENSITIVE)); // 344.xml:  He does admit to heavy drinking
    	
//    	NEGATIVE_MARKERS.add(Pattern.compile("stopped drinking", Pattern.CASE_INSENSITIVE)); // 105.xml: heavy drinking for 40 years, cut down in 2146 and has stopped drinking
    	
    }
	
	
	
	
    @Override
    public Eligibility isMet(Patient p) {
//        return Eligibility.NOT_MET;
    	return findAnyPattern(p.getText(), POSITIVE_MARKERS) ? Eligibility.MET : Eligibility.NOT_MET;
    }
}