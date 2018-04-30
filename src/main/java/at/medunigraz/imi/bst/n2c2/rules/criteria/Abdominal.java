package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Abdominal extends BaseClassifiable {

    private static final List<Pattern> POSITIVE_MARKERS = new ArrayList<>();
    static {
        POSITIVE_MARKERS.add(Pattern.compile("bowel surgery", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("polypectomy", Pattern.CASE_INSENSITIVE)); // Disabled by @kasac
//        POSITIVE_MARKERS.add(Pattern.compile("resection", Pattern.CASE_INSENSITIVE));   // Disabled by @kasac
        POSITIVE_MARKERS.add(Pattern.compile("splenectomy", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("intestine resection", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("intestinal resection", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("bowel resection", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("hysterectomy", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("liver transplant", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("pancreatectomy", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("liver surgery", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("gastric resection", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("gastrectomy", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("hepatectomy", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("appendectomy", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("colostomy", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("cholecystectomy", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("colectomy", Pattern.CASE_INSENSITIVE));
//        POSITIVE_MARKERS.add(Pattern.compile("tracheostomy", Pattern.CASE_INSENSITIVE));

        POSITIVE_MARKERS.add(Pattern.compile("nephrectomy", Pattern.CASE_INSENSITIVE));
//        POSITIVE_MARKERS.add(Pattern.compile("transperitoneal *ctomy", Pattern.CASE_INSENSITIVE));
        // does the above line detect the following ngram 'transperitoneal laparoscopic radical nephrectomy'?
    }

    @Override
    public Eligibility isMet(Patient p) {
        return findAnyPattern(p.getText(), POSITIVE_MARKERS) ? Eligibility.MET : Eligibility.NOT_MET;
    }
}
