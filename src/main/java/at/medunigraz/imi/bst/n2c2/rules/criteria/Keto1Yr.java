package at.medunigraz.imi.bst.n2c2.rules.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class Keto1Yr extends BaseClassifiable {

    private static final List<Pattern> POSITIVE_MARKERS = new ArrayList<>();

    static {

//    	POSITIVE_MARKERS.add(Pattern.compile("NON-AG ACIDOSIS", Pattern.CASE_INSENSITIVE)); // 291.xml: NON-AG ACIDOSIS: related to ileostomy?

    }


    @Override
    public Eligibility isMet(Patient p) {
//        return Eligibility.NOT_MET;

        return findAnyPattern(p.getText(), POSITIVE_MARKERS) ? Eligibility.MET : Eligibility.NOT_MET;

    }
}