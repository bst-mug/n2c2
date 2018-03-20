package at.medunigraz.imi.bst.n2c2.model;

public enum Eligibility {
    MET,
    NOT_MET;

    public static Eligibility get(String value) {
        return Eligibility.valueOf(value.replace(' ', '_').toUpperCase());
    }

}
