package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.PatientVisits;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseClassifiable implements Classifiable {

    private static final Logger LOG = LogManager.getLogger();

    protected final boolean findAnyPattern(String text, List<Pattern> markers) {
        return countPatterns(text, markers) >= 1;
    }

    protected final int countPatterns(String text, List<Pattern> markers) {
        int count = 0;
        for (Pattern positiveMarker : markers) {
            if (positiveMarker.matcher(text).find()) {
                LOG.debug("'{}' fired.", positiveMarker);
                count++;
            }
        }
        return count;
    }

    protected final boolean findAnyPatternInRecentPast(Patient patient, List<Pattern> markers, int months) {
        ArrayList<PatientVisits> visits = patient.getMultipleVisits(months);
        for (PatientVisits visit : visits) {
            String text = visit.getVisit_text();
            if (findAnyPattern(text, markers)) {
                return true;
            }
        }

        return false;
    }

    protected List<Double> findAllValues(String text, Pattern regex) {
        List<Double> ret = new ArrayList<>();

        Matcher matcher = regex.matcher(text);
        while (matcher.find()) {
            String group = matcher.group(1);
            LOG.debug("Got a match for '{}': {}", regex, group);
            ret.add(parseValue(group));
        }

        return ret;
    }

    private double parseValue(String group) {
        double value = 0;
        try {
            value = Double.valueOf(group);
        } catch (NumberFormatException e) {
            LOG.error("Could not parse {} into a double", group);
        }
        return value;
    }
}
