package at.medunigraz.imi.bst.n2c2.model;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class PatientTest {

    @Test
    public void fromXML() {
        File sample = new File(getClass().getResource("/gold-standard/sample.xml").getPath());
        Patient patient = Patient.fromXML(sample);

        assertEquals(Eligibility.NOT_MET, patient.getEligibility("ABDOMINAL"));
        assertEquals(Eligibility.MET, patient.getEligibility("ADVANCED-CAD"));
    }
}