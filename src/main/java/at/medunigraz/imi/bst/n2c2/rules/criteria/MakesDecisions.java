package at.medunigraz.imi.bst.n2c2.rules.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class MakesDecisions extends BaseClassifiable {

    private static final List<Pattern> NEGATIVE_MARKERS = new ArrayList<>();

    static {
//        NEGATIVE_MARKERS.add(Pattern.compile("altered mental", Pattern.CASE_INSENSITIVE)); // 308.xml: CHIEF COMPLAINT: Altered mental status 
        NEGATIVE_MARKERS.add(Pattern.compile("severe dementia", Pattern.CASE_INSENSITIVE)); // 357.xml: 80 year old female with severe dementia and ESRD
//        NEGATIVE_MARKERS.add(Pattern.compile("(Alzheimer|Alzheimers) (meds|medication)", Pattern.CASE_INSENSITIVE)); // 357.xml: Dementia - Cont Alzheimers meds#
//        NEGATIVE_MARKERS.add(Pattern.compile("Alzheimer", Pattern.CASE_INSENSITIVE)); // 357.xml: Dementia - Cont Alzheimers meds#
        //NEGATIVE_MARKERS.add(Pattern.compile("(Alzheimer|Alzheimers)", Pattern.CASE_INSENSITIVE));

    }


    @Override
    public Eligibility isMet(Patient p) {
        return findAnyPattern(p.getCleanedText(), NEGATIVE_MARKERS) ? Eligibility.NOT_MET : Eligibility.MET;
    }
}