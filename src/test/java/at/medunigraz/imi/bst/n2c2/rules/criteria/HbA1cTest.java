package at.medunigraz.imi.bst.n2c2.rules.criteria;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HbA1cTest {

    @Test
    public void regex() {
        HbA1c hba1c = new HbA1c();

        assertTrue(HbA1c.REGEX.matcher("Hemoglobin A1C 8.5").find());   // 117.xml
        assertTrue(HbA1c.REGEX.matcher("hemoglobin A1c was 7.4 %").find()); // 147.xml
        assertTrue(HbA1c.REGEX.matcher("HBA1C 7.2").find()); // 367.xml
        assertTrue(HbA1c.REGEX.matcher("Hgb A1c 7.30").find()); // sample.xml
        //assertTrue(HbA1c.REGEX.matcher("A1Cs &#8211; 7.70% in 3/2113").find()); // 392.xml
        //assertTrue(HbA1c.REGEX.matcher("hemoglobin A1C of 6.20").find()); // 169.xml (gold standard error?)
        //assertTrue(HbA1c.REGEX.matcher("recent a1c 8%").find()); // 125.xml
    }
}