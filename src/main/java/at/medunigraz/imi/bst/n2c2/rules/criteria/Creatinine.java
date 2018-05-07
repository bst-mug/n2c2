package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Creatinine extends BaseClassifiable {

    //final public String[] a_CriterionID_creatinine = {"Creatinine", "crea", "cre", "Cr", "CRE", "CREA"};

    // 101.xml: 1.1 => NOT_MET
    // 225.xml: 1.2 => NOT_MET
    // 272.xml: 1.4 => NOT_MET
    // 144.xml: 1.43 => MET
    private static final double MIN_VALUE = 1.4;

    // Sanity check
    private static final double MAX_VALUE = 10;

    private static final List<Pattern> POSITIVE_MARKERS = new ArrayList<>();
    // TODO check whether creatinine > 10 is possible
    // 313.xml (MET): mcalb/cr 200
    // 250.xml: creatinine 2
    // 268.xml: CRE       1.6
    // 107.xml: creatinine of  1.69
    // 105.xml: Cr 1.4
    // 100.xml: Creatinine             1.0
    // 313.xml (MET): elevated creatinine
    private static final Pattern VALUE_REGEX = Pattern.compile("[^/](?:cr|creatinine|cre)[^a-z].{0,15}?([0-9]\\.[0-9](?:[0-9])?)", Pattern.CASE_INSENSITIVE);

    static {
        // 299.xml: rising serum creatinine
        POSITIVE_MARKERS.add(Pattern.compile("(elevated|rising|high).{1,10}creatinine", Pattern.CASE_INSENSITIVE));

        // 356.xml: creatinine has been high
        POSITIVE_MARKERS.add(Pattern.compile("creatinine.{1,10}(elevated|rising|high)", Pattern.CASE_INSENSITIVE));
    }

    @Override
    public Eligibility isMet(Patient p) {
        List<Double> values = findAllValues(p.getCleanedText(), VALUE_REGEX);

        for (Double value : values) {
            if (value > MIN_VALUE && value < MAX_VALUE) {
                return Eligibility.MET;
            }
        }

        return findAnyPattern(p.getCleanedText(), POSITIVE_MARKERS) ? Eligibility.MET : Eligibility.NOT_MET;
    }
}