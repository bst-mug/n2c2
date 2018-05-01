package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.List;
import java.util.regex.Pattern;

public class Creatinine extends BaseClassifiable {

    //final public String[] a_CriterionID_creatinine = {"Creatinine", "crea", "cre", "Cr", "CRE", "CREA"};

    /**
     * According to the provided annotations, 1.1 is still NOT_MET
     */
    private static final double MAX_VALUE = 1.1;

    // TODO check whether creatinine > 10 is possible
    // 107.xml: creatinine of  1.69
    // 105.xml: Cr 1.4
    // 100.xml: Creatinine             1.0
    private static final Pattern REGEX = Pattern.compile("(?:cr|creatinine) ([0-9]\\.[0-9])", Pattern.CASE_INSENSITIVE);

    @Override
    public Eligibility isMet(Patient p) {
        List<Double> values = findAllValues(p.getText(), REGEX);

        for (Double value : values) {
            if (value > MAX_VALUE) {
                return Eligibility.MET;
            }
        }

        return Eligibility.NOT_MET;
    }
}