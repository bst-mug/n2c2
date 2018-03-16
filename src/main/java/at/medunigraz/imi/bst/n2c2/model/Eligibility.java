package at.medunigraz.imi.bst.n2c2.model;

import java.util.HashMap;
import java.util.Map;

public enum Eligibility {
    MET("met"),
    NOT_MET("not met");

    /**
     * Reverse lookup map
     */
    private static final Map<String, Eligibility> lookup = new HashMap<>();

    static {
        for (Eligibility env : Eligibility.values()) {
            lookup.put(env.getValue(), env);
        }
    }

    private String value;

    Eligibility(String value) {
        this.value = value;
    }

    public static Eligibility get(String value) {
        return lookup.get(value);
    }

    public String getValue() {
        return value;
    }
}
