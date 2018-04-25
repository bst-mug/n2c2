package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Dietsupp2mos extends BaseClassifiable {

    private static final List<Pattern> POSITIVE_MARKERS = new ArrayList<>();

    static {
        // Vitamin D is excluded
        POSITIVE_MARKERS.add(Pattern.compile("folate", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("calcium carbonate", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("supplement", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("multivitamin", Pattern.CASE_INSENSITIVE));
    }

    @Override
    public Eligibility isMet(Patient p) {
        return findAnyPattern(p.getText(), POSITIVE_MARKERS) ? Eligibility.MET : Eligibility.NOT_MET;
    }
}
