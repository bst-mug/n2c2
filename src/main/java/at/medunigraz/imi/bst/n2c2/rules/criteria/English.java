package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class English extends BaseClassifiable {

    private static final List<Pattern> NEGATIVE_MARKERS = new ArrayList<>();

    static {
        NEGATIVE_MARKERS.add(Pattern.compile("spanish", Pattern.CASE_INSENSITIVE)); // 117.xml: Spanish-speaking
        NEGATIVE_MARKERS.add(Pattern.compile("with interpreter", Pattern.CASE_INSENSITIVE)); // 242.xml: conducted with interpreter present
        NEGATIVE_MARKERS.add(Pattern.compile("translated", Pattern.CASE_INSENSITIVE));// 371.xml: South Korea who has been in Canada for one year, translated by
        NEGATIVE_MARKERS.add(Pattern.compile("-speaking", Pattern.CASE_INSENSITIVE));   // 329.xml: Indonesian-speaking
    }

    @Override
    public Eligibility isMet(Patient p) {
        return findAnyPattern(p.getCleanedText(), NEGATIVE_MARKERS) ? Eligibility.NOT_MET : Eligibility.MET;
    }
}