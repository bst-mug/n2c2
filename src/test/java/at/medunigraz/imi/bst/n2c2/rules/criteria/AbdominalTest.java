package at.medunigraz.imi.bst.n2c2.rules.criteria;

import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.junit.Test;

import static org.junit.Assert.*;

public class AbdominalTest {

    @Test
    public void isMet() {
        Patient p = new Patient().withText("Had a bowel surgery last year.");

        assertEquals(Eligibility.MET, (new Abdominal().isMet(p)));
    }
}