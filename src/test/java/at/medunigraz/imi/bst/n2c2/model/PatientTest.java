package at.medunigraz.imi.bst.n2c2.model;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class PatientTest {

    @Test
    public void fromXML() {
        File sample = new File(getClass().getResource("/gold-standard/sample.xml").getPath());
        Patient patient = Patient.fromXML(sample);

        assertEquals(Criterion.NOT_MET, patient.getCriterion("ABDOMINAL"));
        assertEquals(Criterion.MET, patient.getCriterion("ADVANCED-CAD"));
    }
}