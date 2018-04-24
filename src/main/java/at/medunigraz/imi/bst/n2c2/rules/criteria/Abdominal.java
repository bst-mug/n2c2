package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Abdominal implements Classifiable {

    private static final List<Pattern> POSITIVE_MARKERS = new ArrayList<>();
    static {
        POSITIVE_MARKERS.add(Pattern.compile("bowel surgery", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("polypectomy", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("resection", Pattern.CASE_INSENSITIVE));
    }

    @Override
    public Eligibility is_met(Patient p) {
        return findAnyPattern(p.getText()) ? Eligibility.MET : Eligibility.NOT_MET;
    }

    private boolean findAnyPattern(String text) {
        for (Pattern positiveMarker : POSITIVE_MARKERS) {
            if (positiveMarker.matcher(text).find()) {
                return true;
            }
        }
        return false;
    }
}
