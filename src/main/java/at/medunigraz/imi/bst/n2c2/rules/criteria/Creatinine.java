package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

import java.util.List;
import java.util.regex.Pattern;

public class Creatinine extends BaseClassifiable {

    //final public String[] a_CriterionID_creatinine = {"Creatinine", "crea", "cre", "Cr", "CRE", "CREA"};

    // 101.xml: 1.1 => NOT_MET
    // 225.xml: 1.2 => NOT_MET
    // 272.xml: 1.4 => NOT_MET
    // 144.xml: 1.43 => MET
    private static final double MAX_VALUE = 1.4;

    // TODO check whether creatinine > 10 is possible
    // 313.xml: mcalb/cr 200
    // 250.xml: creatinine 2
    // 268.xml: CRE       1.6
    // 107.xml: creatinine of  1.69
    // 105.xml: Cr 1.4
    // 100.xml: Creatinine             1.0
    private static final Pattern REGEX = Pattern.compile("(?:cr|creatinine|cre)(?: \\(Stat Lab\\))? +(?:of |is |stable at )?([0-9]\\.[0-9])", Pattern.CASE_INSENSITIVE);

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