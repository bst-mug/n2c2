package at.medunigraz.imi.bst.n2c2.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigTest {

    @Test
    public void getSVMCost() {
        assertEquals(1, Config.SVM_COST_MAKES_DECISIONS, 0.00001);
    }
}