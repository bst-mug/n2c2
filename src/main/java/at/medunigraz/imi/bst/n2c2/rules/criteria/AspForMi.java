package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AspForMi extends BaseClassifiable {

    private static final List<Pattern> ASPIRIN_MARKERS = new ArrayList<>();
    static {
        // NASAL
        // 186.xml: ASA Physical Status
        ASPIRIN_MARKERS.add(Pattern.compile("(?<!(allerg.{1,30}|[a-z]))asa(?!([a-z]| phy))", Pattern.CASE_INSENSITIVE));
        ASPIRIN_MARKERS.add(Pattern.compile("(?<!(allerg.{1,30}))aspirin(?!.{1,15}(avoid|none|rash))", Pattern.CASE_INSENSITIVE));
        ASPIRIN_MARKERS.add(Pattern.compile("acetylsal", Pattern.CASE_INSENSITIVE));
    }

    @Override
    public Eligibility isMet(Patient p) {
        return findAnyPattern(p.getCleanedText(), ASPIRIN_MARKERS) ? Eligibility.MET : Eligibility.NOT_MET;
    }
}
