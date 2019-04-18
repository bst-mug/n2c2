package at.medunigraz.imi.bst.n2c2.model;

/**
 * Enum types should be the same as XML with hyphen changed to underscore.
 */
public enum Criterion {
    ABDOMINAL(0),
    ADVANCED_CAD(1),
    ALCOHOL_ABUSE(2),
    ASP_FOR_MI(3),
    CREATININE(4),
    DIETSUPP_2MOS(5),
    DRUG_ABUSE(6),
    ENGLISH(7),
    HBA1C(8),
    KETO_1YR(9),
    MAJOR_DIABETES(10),
    MAKES_DECISIONS(11),
    MI_6MOS(12),
    OVERALL_MICRO(90),
    OVERALL_MACRO(91);

    private final int value;

    private Criterion(int value) {
        this.value = value;
    }

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

    public int getValue() {
        return value;
    }
}
