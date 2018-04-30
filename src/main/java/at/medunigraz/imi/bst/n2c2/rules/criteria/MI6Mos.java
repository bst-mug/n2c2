package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MI6Mos extends BaseClassifiable {
    private static final List<Pattern> POSITIVE_MARKERS = new ArrayList<>();

    private static final int PAST_MONTHS = 6;

    static {
        POSITIVE_MARKERS.add(Pattern.compile("STEMI"));
        //POSITIVE_MARKERS.add(Pattern.compile("IMI"));
    }

    @Override
    public Eligibility isMet(Patient p) {
        return findAnyPatternInRecentPast(p, POSITIVE_MARKERS, PAST_MONTHS) ? Eligibility.MET : Eligibility.NOT_MET;
    }
}