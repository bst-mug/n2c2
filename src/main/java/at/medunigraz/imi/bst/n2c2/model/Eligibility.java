package at.medunigraz.imi.bst.n2c2.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum Eligibility {
    MET,
    NOT_MET,
    OVERALL;

    public static Eligibility get(String value) {
        return Eligibility.valueOf(value.replace(' ', '_').toUpperCase());
    }

    @Override
    public String toString() {
        return name().replace('_', ' ').toLowerCase();
    }

    public static Eligibility[] classifiableValues() {
        Set<Eligibility> ret = new HashSet<>(Arrays.asList(values()));
        ret.remove(Eligibility.OVERALL);
        return ret.toArray(new Eligibility[]{});
    }
}
