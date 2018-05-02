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

    private static final int MIN_MARKERS = 2;
    private static final int MIN_MEDICATIONS = 2;

    private static final List<Pattern> DRUG_MARKERS = new ArrayList<>();
    private static final List<Pattern> MI_MARKERS = new ArrayList<>();
    private static final List<Pattern> ANGINA_MARKERS = new ArrayList<>();
    static final List<Pattern> ISCHEMIA_MARKERS = new ArrayList<>();

    static {
        DRUG_MARKERS.add(Pattern.compile("captopril", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("lipitor", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("lopressor", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("nitropatch", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("enalapril", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("lasix", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("hctz", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("hydrochlorothiazide", Pattern.CASE_INSENSITIVE)); // TODO also spelled as "HYDROCLOROTH"
        DRUG_MARKERS.add(Pattern.compile("atorvastatin", Pattern.CASE_INSENSITIVE));    // TODO maybe match on *statin?
        DRUG_MARKERS.add(Pattern.compile("atenolol", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("enalapril maleate", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("diltiazem", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("zestril", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("mevacor", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("lovastatin", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("nitropaste", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("amlodipine", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("cozaar", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("zocor", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("plavix", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("metoprolol", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("lisinopril", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("isinorpill", Pattern.CASE_INSENSITIVE));
        DRUG_MARKERS.add(Pattern.compile("toprol", Pattern.CASE_INSENSITIVE));  // Matches also metoprolol above
        //DRUG_MARKERS.add(Pattern.compile("cardia", Pattern.CASE_INSENSITIVE));    // TODO test it
    }

    static {
        //MI_MARKERS.add(Pattern.compile("CAD"));
        MI_MARKERS.add(Pattern.compile("STEMI"));
        //        MI_MARKERS.add(Pattern.compile("MI"));  // TODO check for fp

        // 356.xml: PRELIMINARY
        MI_MARKERS.add(Pattern.compile("IMI"));     // TODO check for false positives
        MI_MARKERS.add(Pattern.compile("myocardial infarction"));
    }

    static {
        DRUG_MARKERS.add(Pattern.compile("anginal", Pattern.CASE_INSENSITIVE)); // TODO test angin.*
        //ANGINA_MARKERS.add(Pattern.compile("chest pain", Pattern.CASE_INSENSITIVE));  // Leads to many fp due to negations
        ANGINA_MARKERS.add(Pattern.compile("unstable angina", Pattern.CASE_INSENSITIVE));
        ANGINA_MARKERS.add(Pattern.compile("current angina", Pattern.CASE_INSENSITIVE));      // FIXME any angina?
        ANGINA_MARKERS.add(Pattern.compile("angina pectoris", Pattern.CASE_INSENSITIVE));   // 242.xml
        //ANGINA_MARKERS.add(Pattern.compile("EKG changes", Pattern.CASE_INSENSITIVE));   // Really?
    }

    static {
        // "?<!" is a negative lookbehind
        // "?!" is a negative lookahead
        // (https://www.regular-expressions.info/lookaround.html)
        ISCHEMIA_MARKERS.add(Pattern.compile("(?<!(no [a-z ]{0,30}|bowel |limb ))ischemia(?!( colitis))", Pattern.CASE_INSENSITIVE));    // TODO maybe merge with ischemi.*
        //ISCHEMIA_MARKERS.add(Pattern.compile("dyspnea", Pattern.CASE_INSENSITIVE));    // TODO test it
    }



    @Override
    public Eligibility isMet(Patient p) {
        int countAdvanced = 0;
        
        countAdvanced += countPatterns(p.getText(), DRUG_MARKERS) >= MIN_MEDICATIONS ? 1 : 0;
        countAdvanced += findAnyPattern(p.getText(), MI_MARKERS) ? 1 : 0;
        countAdvanced += findAnyPattern(p.getText(), ANGINA_MARKERS) ? 1 : 0;
        countAdvanced += findAnyPattern(p.getText(), ISCHEMIA_MARKERS) ? 1 : 0;

        return countAdvanced >= MIN_MARKERS ? Eligibility.MET : Eligibility.NOT_MET;
    }
}
