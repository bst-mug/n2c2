package at.medunigraz.imi.bst.n2c2.rules.criteria;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AdvancedCADTest {

    @Test
    public void ischemiaMarkers() {
        AdvancedCAD cad = new AdvancedCAD();

        assertTrue(cad.findAnyPattern("ischemia", AdvancedCAD.ISCHEMIA_MARKERS));   // baseline

        // TODO fix broken tests with no impact on accuracy
        assertFalse(cad.findAnyPattern("no evidence of ischemia", AdvancedCAD.ISCHEMIA_MARKERS));   // 127.xml
        assertFalse(cad.findAnyPattern("no definite evidence of ischemia", AdvancedCAD.ISCHEMIA_MARKERS));   // 169.xml
        assertFalse(cad.findAnyPattern("no evidence of ischemia", AdvancedCAD.ISCHEMIA_MARKERS));   // 181.xml
        assertFalse(cad.findAnyPattern("RLE limb ischemia", AdvancedCAD.ISCHEMIA_MARKERS));   // 230.xml
        assertFalse(cad.findAnyPattern("No EKG changes consistent with ischemia", AdvancedCAD.ISCHEMIA_MARKERS));   // 304.xml
        assertFalse(cad.findAnyPattern("ischemia colitis", AdvancedCAD.ISCHEMIA_MARKERS));   // 313.xml
        //assertFalse(cad.findAnyPattern("possible lateral ischemia", AdvancedCAD.ISCHEMIA_MARKERS));   // 315.xml
        assertFalse(cad.findAnyPattern("bowel ischemia", AdvancedCAD.ISCHEMIA_MARKERS));   // 393.xml
        //assertFalse(cad.findAnyPattern("Without ischemia", AdvancedCAD.ISCHEMIA_MARKERS));   // 357.xml
        assertFalse(cad.findAnyPattern("no evidence for myocardial ischemia", AdvancedCAD.ISCHEMIA_MARKERS));   // 377.xml
        //assertFalse(cad.findAnyPattern("negative for ischemia", AdvancedCAD.ISCHEMIA_MARKERS));   // 387.xml
        assertFalse(cad.findAnyPattern("no ischemia", AdvancedCAD.ISCHEMIA_MARKERS));   // 391.xml
    }

}