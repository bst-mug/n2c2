package at.medunigraz.imi.bst.n2c2.fasttext;

import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FastTextFacadeTest {

    @Test
    public void trainAndPredict() {
        Map<String, String> trainData = new TreeMap<>();
        trainData.put("This is a test", "MET");

        assertTrue(FastTextFacade.train(trainData));
        assertEquals("MET", FastTextFacade.predict("This is a test"));
    }
}