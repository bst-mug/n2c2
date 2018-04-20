package at.medunigraz.imi.bst.n2c2.preprocess.conceptmapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Michel Oleynik <michel.oleynik@stud.medunigraz.at>
 * @link https://github.com/michelole/reassess/blob/master/src/main/java/at/medunigraz/imi/reassess/conceptmapper/ConceptMapper.java
 */
public interface ConceptMapper {
    List<String> map(String text);

    String annotate(String text);

    default Set<String> uniqueMap(String text) {
        return new HashSet<String>(map(text));
    }
}
