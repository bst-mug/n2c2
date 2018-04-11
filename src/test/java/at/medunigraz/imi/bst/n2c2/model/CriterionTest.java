package at.medunigraz.imi.bst.n2c2.model;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CriterionTest {

    @Test
    public void isValid() {
        assertTrue(Criterion.isValid("Abdominal"));
        assertTrue(Criterion.isValid("Advanced-cad"));
        assertTrue(Criterion.isValid("Overall (micro)"));
    }
}