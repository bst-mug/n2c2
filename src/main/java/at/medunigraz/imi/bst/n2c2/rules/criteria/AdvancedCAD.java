package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AdvancedCAD extends BaseClassifiable {

//    final public String[] a_CriterionID_advanced_cad = {"cad", "advanced cad", "CAD", "captopril", "lipitor", "Lopressor",
//            "Lipitor", "ischemic equivalent", "CAD, s/p MI", "nitropatch", "chest  pain", "chest pain", "EKG changes",
//            "Metoprolol", "Cardia", "Ischemia", "ischemia", "Cozaar", "MI", "anginal  symptoms", "anginal", "Zocor",
//            "Plavix", "unstable angina", "LISINOPRIL", "nferior STEMI", "Atenolol", "Diltiazem", "Zestril", "Mevacor",
//            "lovastatin", "Lovastatin", "nitropaste", "Nitropaste", "amlodipine", "IMI", "isinorpill", "Toprol-XL", "Toprol",
//            "enalapril", "Lasix", "HCTZ", "Hctz", "HYDROCHLOROTHIAZIDE", "ATORVASTATIN", "ATENOLOL", "Enalapril Maleate",
//            "inferior ischemia"};

    private static final List<Pattern> POSITIVE_MARKERS = new ArrayList<>();
    static {
        // From the annotated samples
        POSITIVE_MARKERS.add(Pattern.compile("cad", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("captopril", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("lipitor", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("lopressor", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("nitropatch", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("chest pain", Pattern.CASE_INSENSITIVE));  // TODO test with \p{javaWhitespace}
        //POSITIVE_MARKERS.add(Pattern.compile("EKG changes", Pattern.CASE_INSENSITIVE));   // Really?
        POSITIVE_MARKERS.add(Pattern.compile("metoprolol", Pattern.CASE_INSENSITIVE));
        //POSITIVE_MARKERS.add(Pattern.compile("cardia", Pattern.CASE_INSENSITIVE));    // TODO test it
        POSITIVE_MARKERS.add(Pattern.compile("ischemia", Pattern.CASE_INSENSITIVE));    // TODO maybe merge with ischemi.*
        POSITIVE_MARKERS.add(Pattern.compile("cozaar", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("mi", Pattern.CASE_INSENSITIVE));  // TODO check for fp
        POSITIVE_MARKERS.add(Pattern.compile("anginal", Pattern.CASE_INSENSITIVE)); // TODO test angin.*
        POSITIVE_MARKERS.add(Pattern.compile("zocor", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("plavix", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("unstable angina", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("lisinopril", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("stemi", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("diltiazem", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("zestril", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("mevacor", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("lovastatin", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("nitropaste", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("amlodipine", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("imi", Pattern.CASE_INSENSITIVE));     // TODO check for false positives
        POSITIVE_MARKERS.add(Pattern.compile("isinorpill", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("toprol", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("enalapril", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("lasix", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("hctz", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("hydrochlorothiazide", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("atorvastatin", Pattern.CASE_INSENSITIVE));    // TODO maybe match on *statin?
        POSITIVE_MARKERS.add(Pattern.compile("atenolol", Pattern.CASE_INSENSITIVE));
        POSITIVE_MARKERS.add(Pattern.compile("enalapril maleate", Pattern.CASE_INSENSITIVE));
    }

    @Override
    public Eligibility is_met(Patient p) {
        // TODO According to the guidelines, we should check for *two or more* of the criteria.
        // Check if it improves overall metrics though.
        return findAnyPattern(p.getText(), POSITIVE_MARKERS) ? Eligibility.MET : Eligibility.NOT_MET;
    }
}
