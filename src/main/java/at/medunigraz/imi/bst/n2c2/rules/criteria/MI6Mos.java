package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MI6Mos extends BaseClassifiable {
    private static final List<Pattern> POSITIVE_MARKERS = new ArrayList<>();

    static {
        POSITIVE_MARKERS.add(Pattern.compile("stemi", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("imi", Pattern.CASE_INSENSITIVE));     // TODO check for false positives
    }

    @Override
    public Eligibility is_met(Patient p) {
        return findAnyPattern(p.getText(), POSITIVE_MARKERS) ? Eligibility.MET : Eligibility.NOT_MET;
    }
}