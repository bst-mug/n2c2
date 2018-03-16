package at.medunigraz.imi.bst.n2c2.evaluator;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class InterAnnotatorAgreementTest {

    private static final String GOLD = "/gold-standard/";
    private static final String RESULTS = "/results/";

    // TODO skip if iaa.py not present
    // TODO skip if samples not present

    @Test
    public void evaluate() {
        File goldStandard = new File(getClass().getResource(GOLD).getFile());
        File results = new File(getClass().getResource(RESULTS).getFile());

        InterAnnotatorAgreement iaa = new InterAnnotatorAgreement(goldStandard, results);
        assertEquals(1, iaa.getF1(), 0.00001);
    }
}