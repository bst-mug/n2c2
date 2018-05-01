package at.medunigraz.imi.bst.n2c2.model;

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
    OVERALL_MICRO,
    OVERALL_MACRO;

    public static Criterion get(String value) {
        String cleanedValue = value.replaceAll("[()]", "").replace('-', '_').replace(' ', '_');
        return valueOf(cleanedValue.toUpperCase());
    }

    public static boolean isValid(String value) {
        try {
            get(value);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name().replace('_', '-');
    }

    /**
     * The same as values(), but without OVERALL_MICRO and OVERALL_MACRO
     *
     * @return
     */
    public static Criterion[] classifiableValues() {
        Criterion[] values = values();
        Criterion[] ret = new Criterion[values.length - 2];

        int i = 0;
        for (Criterion value : values) {
            if (value != Criterion.OVERALL_MICRO && value != Criterion.OVERALL_MACRO) {
                ret[i++] = value;
            }
        }

        return ret;
    }
}
