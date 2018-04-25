package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class MakesDecisions extends BaseClassifiable {

    @Override
    public Eligibility isMet(Patient p) {
        return Eligibility.MET;
    }
}