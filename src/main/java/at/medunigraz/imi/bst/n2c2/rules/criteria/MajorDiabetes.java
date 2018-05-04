package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MajorDiabetes extends BaseClassifiable {

    private static final List<Pattern> COMPLICATION_MARKERS = new ArrayList<>();
    static {
        // From the guidelines
        COMPLICATION_MARKERS.add(Pattern.compile("amputation", Pattern.CASE_INSENSITIVE));
        COMPLICATION_MARKERS.add(Pattern.compile("kidney damage", Pattern.CASE_INSENSITIVE));
        //COMPLICATION_MARKERS.add(Pattern.compile("skin condition", Pattern.CASE_INSENSITIVE));    // No clear distinction?
        COMPLICATION_MARKERS.add(Pattern.compile("(?<!(no [a-z ]{0,30}))retinopathy", Pattern.CASE_INSENSITIVE));
        COMPLICATION_MARKERS.add(Pattern.compile("(?<!(no [a-z ]{0,30}))nephropathy", Pattern.CASE_INSENSITIVE));

        // 355.xml (NOT_MET): No evidence of neuropathy
        COMPLICATION_MARKERS.add(Pattern.compile("(?<!(no [a-z ]{0,30}))neuropathy", Pattern.CASE_INSENSITIVE));

        // From the annotated examples
        COMPLICATION_MARKERS.add(Pattern.compile("macular degeneration", Pattern.CASE_INSENSITIVE));
        COMPLICATION_MARKERS.add(Pattern.compile("lue weakness", Pattern.CASE_INSENSITIVE));
        COMPLICATION_MARKERS.add(Pattern.compile("retinal venous occlusion", Pattern.CASE_INSENSITIVE));
        //COMPLICATION_MARKERS.add(Pattern.compile("paresthesias", Pattern.CASE_INSENSITIVE)); // Led to loss in accuracy

        // From the corpus itself
        COMPLICATION_MARKERS.add(Pattern.compile("mellitus major", Pattern.CASE_INSENSITIVE));

        // 185.xml (NOT_MET): suggest radiculopathy
        //COMPLICATION_MARKERS.add(Pattern.compile("radiculopathy", Pattern.CASE_INSENSITIVE));
        //COMPLICATION_MARKERS.add(Pattern.compile("staph", Pattern.CASE_INSENSITIVE));    // 161.xml (no changes)

        // 316.xml: Renal insuff- need to
        // 382.xml (NOT_MET): renal insufficiency
        COMPLICATION_MARKERS.add(Pattern.compile("renal insuff", Pattern.CASE_INSENSITIVE));   // 286.xml

        COMPLICATION_MARKERS.add(Pattern.compile("(?<!(no [a-z ]{0,30}))renal failure", Pattern.CASE_INSENSITIVE));

        //COMPLICATION_MARKERS.add(Pattern.compile("end-stage renal", Pattern.CASE_INSENSITIVE)); // No changes
        //COMPLICATION_MARKERS.add(Pattern.compile("ESRD"));   // Increases tp, but also fp

        COMPLICATION_MARKERS.add(Pattern.compile("worsening kidney", Pattern.CASE_INSENSITIVE));    // 111.xml
        COMPLICATION_MARKERS.add(Pattern.compile("ulceration", Pattern.CASE_INSENSITIVE));   // 372.xml

        // 174.xml: Skin: Chronic ischemic ulcer
        COMPLICATION_MARKERS.add(Pattern.compile("chronic.{0,20}ulcer", Pattern.CASE_INSENSITIVE));

        // 378.xml: Skin:	Telangiectasias
        COMPLICATION_MARKERS.add(Pattern.compile("ectasia", Pattern.CASE_INSENSITIVE));

        COMPLICATION_MARKERS.add(Pattern.compile("kidney disease", Pattern.CASE_INSENSITIVE));
        COMPLICATION_MARKERS.add(Pattern.compile("skin.{0,40}breakdown", Pattern.CASE_INSENSITIVE));    // 362.xml: Skin exam: Notable for slight erythema and breakdown
        COMPLICATION_MARKERS.add(Pattern.compile("blunt trauma", Pattern.CASE_INSENSITIVE));
    }

    @Override
    public Eligibility isMet(Patient p) {
        return (findAnyPattern(p.getCleanedText(), COMPLICATION_MARKERS)) ? Eligibility.MET : Eligibility.NOT_MET;
    }
}