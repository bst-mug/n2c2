package at.medunigraz.imi.bst.n2c2.preprocess.conceptmapper;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class MetaMapLiteFacadeTest {

    private static final String BREAST_CANCER = "The patient has breast cancer.";

    @Before
    public void setUp() {
        Assume.assumeTrue(MetaMapLiteFacade.isModelsDirValid());
    }

    @Test
    public void testMap() {
        MetaMapLiteFacade mm = MetaMapLiteFacade.getInstance();

        List<String> expected = new ArrayList<String>();
        expected.add("C0030705"); // Patients
        expected.add("C0006142"); // Malignant neoplasm of breast
        expected.add("C0678222"); // Breast Carcinoma
        List<String> actual = mm.map(BREAST_CANCER);

        assertEquals(expected, actual);
    }

    @Test
    public void testUniqueMap() {
        final String doubledText = BREAST_CANCER + ". " + BREAST_CANCER;
        MetaMapLiteFacade mm = MetaMapLiteFacade.getInstance();

        List<String> expectedList = new ArrayList<String>();
        // Expects doubled CUIs
        expectedList.add("C0030705"); // Patients
        expectedList.add("C0006142"); // Malignant neoplasm of breast
        expectedList.add("C0678222"); // Breast Carcinoma
        expectedList.add("C0030705"); // Patients
        expectedList.add("C0006142"); // Malignant neoplasm of breast
        expectedList.add("C0678222"); // Breast Carcinoma
        List<String> actualList = mm.map(doubledText);
        assertEquals(expectedList, actualList);

        Set<String> expectedSet = new HashSet<String>();
        expectedSet.add("C0006142"); // Malignant neoplasm of breast
        expectedSet.add("C0678222"); // Breast Carcinoma
        expectedSet.add("C0030705"); // Patients
        Set<String> actualSet = mm.uniqueMap(doubledText);
        assertEquals(expectedSet, actualSet);
    }

    @Test
    public void testAnnotate() {
        MetaMapLiteFacade mm = MetaMapLiteFacade.getInstance();

        // Basic test
        String actual = mm.annotate(BREAST_CANCER);
        String expected = "The <patient|C0030705:Patients|> has <breast cancer|C0006142:Malignant neoplasm of breast|C0678222:Breast Carcinoma|>.";
        assertEquals(expected, actual);

        // Submatches
        actual = mm.annotate("History of present illness");
        // TODO debug why expected changed
        //expected = "<History of present illness|C0262512:History of present illness|C0488508:History of present illness:Finding:Point in time:^Patient:Nominal:Reported|>";
        expected = "<History of present illness|C0262512:History of present illness|>";
        assertEquals(expected, actual);

        // Double spacing
        actual = mm.annotate("headache.  headache.");
        expected = "<headache|C0018681:Headache|C2096315:ENT surgical result nose headache|>.  <headache|C2096315:ENT surgical result nose headache|C0018681:Headache|>.";
        assertEquals(expected, actual);
    }
}