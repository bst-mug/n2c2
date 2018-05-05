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
        //DRUG_MARKERS.add(Pattern.compile("lipitor", Pattern.CASE_INSENSITIVE));     // 377 times
        DRUG_MARKERS.add(Pattern.compile("lisinopril", Pattern.CASE_INSENSITIVE));  // 375 times
        //DRUG_MARKERS.add(Pattern.compile("lasix", Pattern.CASE_INSENSITIVE));       // 319 times
        DRUG_MARKERS.add(Pattern.compile("atenolol", Pattern.CASE_INSENSITIVE));    // 310 times
        DRUG_MARKERS.add(Pattern.compile("toprol", Pattern.CASE_INSENSITIVE));      // 258 times  // Matches also metoprolol
        DRUG_MARKERS.add(Pattern.compile("plavix", Pattern.CASE_INSENSITIVE));      // 229 times
        DRUG_MARKERS.add(Pattern.compile("metoprolol", Pattern.CASE_INSENSITIVE));  // 159 times
        //DRUG_MARKERS.add(Pattern.compile("lopressor", Pattern.CASE_INSENSITIVE));   // 155 times
        DRUG_MARKERS.add(Pattern.compile("hctz", Pattern.CASE_INSENSITIVE));        // 134 times
        DRUG_MARKERS.add(Pattern.compile("hydrochlorothiazide", Pattern.CASE_INSENSITIVE)); // 100 times // TODO also spelled as "HYDROCLOROTH" (3 times)
        DRUG_MARKERS.add(Pattern.compile("atorvastatin", Pattern.CASE_INSENSITIVE));    // 94 times    // TODO maybe match on *statin?
        DRUG_MARKERS.add(Pattern.compile("zestril", Pattern.CASE_INSENSITIVE));     // 82 times
        DRUG_MARKERS.add(Pattern.compile("amlodipine", Pattern.CASE_INSENSITIVE));  // 72 times
        DRUG_MARKERS.add(Pattern.compile("zocor", Pattern.CASE_INSENSITIVE));       // 71 times
        DRUG_MARKERS.add(Pattern.compile("cozaar", Pattern.CASE_INSENSITIVE));      // 58 times
        DRUG_MARKERS.add(Pattern.compile("captopril", Pattern.CASE_INSENSITIVE));   // 48 times
        DRUG_MARKERS.add(Pattern.compile("diltiazem", Pattern.CASE_INSENSITIVE));   // 45 times
        DRUG_MARKERS.add(Pattern.compile("enalapril", Pattern.CASE_INSENSITIVE));   // 19 times
        DRUG_MARKERS.add(Pattern.compile("lovastatin", Pattern.CASE_INSENSITIVE));  // 9 times
        DRUG_MARKERS.add(Pattern.compile("nitropaste", Pattern.CASE_INSENSITIVE));  // 9 times
        DRUG_MARKERS.add(Pattern.compile("mevacor", Pattern.CASE_INSENSITIVE));     // 9 times
        DRUG_MARKERS.add(Pattern.compile("nitropatch", Pattern.CASE_INSENSITIVE));  // 5 times
        DRUG_MARKERS.add(Pattern.compile("isinorpill", Pattern.CASE_INSENSITIVE));  // 1 time
        //DRUG_MARKERS.add(Pattern.compile("cardia", Pattern.CASE_INSENSITIVE));    // TODO test it
    }

    static {
        //MI_MARKERS.add(Pattern.compile("CAD"));

        MI_MARKERS.add(Pattern.compile("myocardial infarction", Pattern.CASE_INSENSITIVE)); // 87 times

        MI_MARKERS.add(Pattern.compile("STEMI"));   // 60 times
        //        MI_MARKERS.add(Pattern.compile("MI"));  // TODO check for fp

        // 136.xml: EZETIMIBE
        // 356.xml: PRELIMINARY
        MI_MARKERS.add(Pattern.compile("(?<!(EZET|PREL))IMI"));     // 33 times
    }

    static {
        // FIXME any angina?
        //ANGINA_MARKERS.add(Pattern.compile("anginal", Pattern.CASE_INSENSITIVE));     // 21 times // TODO test angin.*
        //ANGINA_MARKERS.add(Pattern.compile("chest pain", Pattern.CASE_INSENSITIVE));  // Leads to many fp due to negations
        ANGINA_MARKERS.add(Pattern.compile("unstable angina", Pattern.CASE_INSENSITIVE));   // 27 times
        ANGINA_MARKERS.add(Pattern.compile("current angina", Pattern.CASE_INSENSITIVE));    // 7 times
        ANGINA_MARKERS.add(Pattern.compile("angina pectoris", Pattern.CASE_INSENSITIVE));   // 7 times // 242.xml
        ANGINA_MARKERS.add(Pattern.compile("chest heaviness", Pattern.CASE_INSENSITIVE));
        //ANGINA_MARKERS.add(Pattern.compile("(?<!(no .{0,30}|bowel |limb |negative for |RLE |digital |enteric |extremity |denies .{0,50}))angina", Pattern.CASE_INSENSITIVE));
        //ANGINA_MARKERS.add(Pattern.compile("(?<!(no.{1,150}|negative for.{1,150}|den.{1,150}|without.{1,30}|abscence of |any.{1,150}))chest pain", Pattern.CASE_INSENSITIVE));
        //ANGINA_MARKERS.add(Pattern.compile("EKG changes", Pattern.CASE_INSENSITIVE));   // Really?
    }

    static {
        // "?<!" is a negative lookbehind
        // "?!" is a negative lookahead
        // (https://www.regular-expressions.info/lookaround.html)
        ISCHEMIA_MARKERS.add(Pattern.compile("(?<!(no .{0,30}|bowel |limb |negative for |RLE |digital |enteric |extremity |denie.{1,50}|without ))ischemia(?!( colitis))", Pattern.CASE_INSENSITIVE));    // TODO maybe merge with ischemi.*
        //ISCHEMIA_MARKERS.add(Pattern.compile("dyspnea", Pattern.CASE_INSENSITIVE));    // TODO test it
    }



    @Override
    public Eligibility isMet(Patient p) {
        int countAdvanced = 0;

        countAdvanced += countPatterns(p.getCleanedText(), DRUG_MARKERS, MIN_MEDICATIONS) >= MIN_MEDICATIONS ? 1 : 0;
        countAdvanced += findAnyPattern(p.getCleanedText(), MI_MARKERS) ? 1 : 0;
        countAdvanced += findAnyPattern(p.getCleanedText(), ANGINA_MARKERS) ? 1 : 0;
        countAdvanced += findAnyPattern(p.getCleanedText(), ISCHEMIA_MARKERS) ? 1 : 0;

        return countAdvanced >= MIN_MARKERS ? Eligibility.MET : Eligibility.NOT_MET;
    }
}
