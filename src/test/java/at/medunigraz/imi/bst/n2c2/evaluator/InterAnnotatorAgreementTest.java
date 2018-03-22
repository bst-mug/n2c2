package at.medunigraz.imi.bst.n2c2.evaluator;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class InterAnnotatorAgreementTest {

    private static final String GOLD = "/gold-standard/";
    private static final String RESULTS = "/results/";

    @Before
    public void SetUp() {
        Assume.assumeTrue(InterAnnotatorAgreement.scriptExists());
    }

    @Test
    public void evaluate() {
        File goldStandard = new File(getClass().getResource(GOLD).getFile());
        File results = new File(getClass().getResource(RESULTS).getFile());

        InterAnnotatorAgreement iaa = new InterAnnotatorAgreement(goldStandard, results);
        assertEquals(1, iaa.getF1(), 0.00001);
    }
}