package at.medunigraz.imi.bst.n2c2.model;

import java.util.HashMap;
import java.util.Map;

public enum Criterion {
    ABDOMINAL("ABDOMINAL"),
    ADVANCE_CAD("ADVANCED-CAD"),
    ALCOHOL_ABUSE("ALCOHOL-ABUSE"),
    ASP_FOR_MI("ASP-FOR-MI"),
    CREATININE("CREATININE"),
    DIETSUPP_2MOS("DIETSUPP-2MOS"),
    DRUG_ABUSE("DRUG-ABUSE"),
    ENGLISH("ENGLISH"),
    HBA1C("HBA1C"),
    KETO_1YR("KETO-1YR"),
    MAJOR_DIABETES("MAJOR-DIABETES"),
    MAKES_DECISIONS("MAKES-DECISIONS"),
    MI_6MOS("MI-6MOS");

    /**
     * Reverse lookup map
     */
    private static final Map<String, Criterion> lookup = new HashMap<>();

    static {
        for (Criterion env : Criterion.values()) {
            lookup.put(env.getValue(), env);
        }
    }

    private String value;

    Criterion(String value) {
        this.value = value;
    }

    public static Criterion get(String value) {
        return lookup.get(value);
    }

    public String getValue() {
        return value;
    }
}
