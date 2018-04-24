package at.medunigraz.imi.bst.n2c2.rules.criteria;

import java.util.List;
import java.util.regex.Pattern;

public abstract class BaseClassifiable implements Classifiable {

    protected final boolean findAnyPattern(String text, List<Pattern> markers) {
        for (Pattern positiveMarker : markers) {
            if (positiveMarker.matcher(text).find()) {
                return true;
            }
        }
        return false;
    }
}
