package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MajorDiabetes extends BaseClassifiable {

    private static final List<Pattern> DIABETES_MARKERS = new ArrayList<>();
    static {
        // From the guidelines
        DIABETES_MARKERS.add(Pattern.compile("diabetes", Pattern.CASE_INSENSITIVE));
        DIABETES_MARKERS.add(Pattern.compile("insulin", Pattern.CASE_INSENSITIVE));
    }

    private static final List<Pattern> COMPLICATION_MARKERS = new ArrayList<>();
    static {
        // From the guidelines
        COMPLICATION_MARKERS.add(Pattern.compile("amputation", Pattern.CASE_INSENSITIVE));
        COMPLICATION_MARKERS.add(Pattern.compile("kidney damage", Pattern.CASE_INSENSITIVE));
        //COMPLICATION_MARKERS.add(Pattern.compile("skin condition", Pattern.CASE_INSENSITIVE));    // No clear distinction?
        COMPLICATION_MARKERS.add(Pattern.compile("retinopathy", Pattern.CASE_INSENSITIVE));
        COMPLICATION_MARKERS.add(Pattern.compile("nephropathy", Pattern.CASE_INSENSITIVE));
        COMPLICATION_MARKERS.add(Pattern.compile("neuropathy", Pattern.CASE_INSENSITIVE));

        // From the annotated examples
        COMPLICATION_MARKERS.add(Pattern.compile("macular degeneration", Pattern.CASE_INSENSITIVE));
        COMPLICATION_MARKERS.add(Pattern.compile("lue weakness", Pattern.CASE_INSENSITIVE));
        COMPLICATION_MARKERS.add(Pattern.compile("retinal venous occlusion", Pattern.CASE_INSENSITIVE));
        //COMPLICATION_MARKERS.add(Pattern.compile("paresthesias", Pattern.CASE_INSENSITIVE)); // Led to loss in accuracy

        // From the corpus itself
        COMPLICATION_MARKERS.add(Pattern.compile("mellitus major", Pattern.CASE_INSENSITIVE));
        COMPLICATION_MARKERS.add(Pattern.compile("radiculopathy", Pattern.CASE_INSENSITIVE));   // 191.xml
        //COMPLICATION_MARKERS.add(Pattern.compile("staph", Pattern.CASE_INSENSITIVE));    // 161.xml (no changes)

        // 316.xml: Renal insuff- need to
        COMPLICATION_MARKERS.add(Pattern.compile("renal insuff", Pattern.CASE_INSENSITIVE));   // 286.xml
        COMPLICATION_MARKERS.add(Pattern.compile("renal failure", Pattern.CASE_INSENSITIVE));
        //COMPLICATION_MARKERS.add(Pattern.compile("end-stage renal", Pattern.CASE_INSENSITIVE)); // No changes
        //COMPLICATION_MARKERS.add(Pattern.compile("ESRD"));   // Increases tp, but also fp

        COMPLICATION_MARKERS.add(Pattern.compile("worsening kidney", Pattern.CASE_INSENSITIVE));    // 111.xml
    }

    @Override
    public Eligibility isMet(Patient p) {
        return (findAnyPattern(p.getText(), DIABETES_MARKERS) && findAnyPattern(p.getText(), COMPLICATION_MARKERS)) ? Eligibility.MET : Eligibility.NOT_MET;
    }
}