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
        // systemic
        // 145.xml (NOT_MET): NSTEMI in March, 2136
        // 250.xml (NOT_MET): STEMI in 2078
        POSITIVE_MARKERS.add(Pattern.compile("(?<!(rule out |prior.{1,15}|(history|hx|fh).{1,80}))stemi[^c](?!.{0,15}[0-9])", Pattern.CASE_INSENSITIVE));

        // 109.xml (NOT_MET): Family history: Notable for her father with myocardial infarction
        // 228.xml (NOT_MET): prior anterolateral myocardial infarction.
        // 154.xml (NOT_MET): rule out myocardial infarction.
        //POSITIVE_MARKERS.add(Pattern.compile("(?<!(rule out |prior.{1,15}|(history|hx|fh).{1,80}))myocardial infarction", Pattern.CASE_INSENSITIVE)); // 87 times

        //POSITIVE_MARKERS.add(Pattern.compile("(?<!(EZET|PREL))IMI"));
    }

    @Override
    public Eligibility isMet(Patient p) {
        return findAnyPatternInRecentPast(p, POSITIVE_MARKERS, PAST_MONTHS) ? Eligibility.MET : Eligibility.NOT_MET;
    }
}