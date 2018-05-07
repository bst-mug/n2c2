package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.regex.Pattern;

public class HbA1c extends BaseClassifiable {

    private static final Logger LOG = LogManager.getLogger();

    private static final double MIN_VALUE = 6.5;
    private static final double MAX_VALUE = 9.5;

    //	final public String[] a_CriterionID_hba1c = {"hba1c","HB Alc","HgAlC","HbA1c","HBA1c", "Hemoglobin A1C", "Hgb A1c", "hemoglobin A1c"};

    // We match only on [0-9] because 10.x would be out anyway...
    static final Pattern REGEX = Pattern.compile("A1C.{0,25}?([0-9]{1,2}\\.[0-9]{1,2})", Pattern.CASE_INSENSITIVE);

	@Override
    public Eligibility isMet(Patient p) {
        List<Double> values = findAllValues(p.getCleanedText(), REGEX);

        for (Double value : values) {
            if (value >= MIN_VALUE && value <= MAX_VALUE) {
                return Eligibility.MET;
            }
        }

		return Eligibility.NOT_MET;
    }
	
}
