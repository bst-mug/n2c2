package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class Keto1Yr extends BaseClassifiable {

    @Override
    public Eligibility is_met(Patient p) {
        return Eligibility.NOT_MET;
    }
}