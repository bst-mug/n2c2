package at.medunigraz.imi.bst.n2c2.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CriterionTest {

    @Test
    public void isValid() {
        assertTrue(Criterion.isValid("Abdominal"));
        assertTrue(Criterion.isValid("Advanced-cad"));
        assertTrue(Criterion.isValid("Overall (micro)"));
    }

    @Test
    public void classifiableValues() {
        assertEquals(Criterion.values().length - 2, Criterion.classifiableValues().length);

        // Check whether the order is preserved
        for (int i = 0; i < Criterion.classifiableValues().length; i++) {
            assertEquals(Criterion.values()[i], Criterion.classifiableValues()[i]);
        }
    }
}