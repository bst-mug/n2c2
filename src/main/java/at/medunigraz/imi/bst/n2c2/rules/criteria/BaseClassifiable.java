package at.medunigraz.imi.bst.n2c2.rules.criteria;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.regex.Pattern;

public abstract class BaseClassifiable implements Classifiable {

    private static final Logger LOG = LogManager.getLogger();

    protected final boolean findAnyPattern(String text, List<Pattern> markers) {
        for (Pattern positiveMarker : markers) {
            if (positiveMarker.matcher(text).find()) {
                LOG.debug("'{}' fired.", positiveMarker);
                return true;
            }
        }
        return false;
    }
}
