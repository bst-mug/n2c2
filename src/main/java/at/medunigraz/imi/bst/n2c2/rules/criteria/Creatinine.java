package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class Creatinine implements Classifiable {

    @Override
    public Eligibility isMet(Patient p) {
        return Eligibility.NOT_MET;
        // TODO Auto-generated method stub

    }
}