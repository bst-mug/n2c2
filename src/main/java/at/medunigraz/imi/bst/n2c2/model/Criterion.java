package at.medunigraz.imi.bst.n2c2.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Enum types should be the same as XML with hyphen changed to underscore.
 */
public enum Criterion {
    ABDOMINAL,
    ADVANCED_CAD,
    ALCOHOL_ABUSE,
    ASP_FOR_MI,
    CREATININE,
    DIETSUPP_2MOS,
    DRUG_ABUSE,
    ENGLISH,
    HBA1C,
    KETO_1YR,
    MAJOR_DIABETES,
    MAKES_DECISIONS,
    MI_6MOS,
    OVERALL;

    public static Criterion get(String value) {
        return valueOf(value.replace('-', '_').toUpperCase());
    }

    @Override
    public String toString() {
        return name().replace('_', '-');
    }

    public static Criterion[] classifiableValues() {
        Set<Criterion> ret = new HashSet<>(Arrays.asList(values()));
        ret.remove(Criterion.OVERALL);
        return ret.toArray(new Criterion[]{});
    }
}
