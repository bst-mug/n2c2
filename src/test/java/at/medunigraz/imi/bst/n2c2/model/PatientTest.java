package at.medunigraz.imi.bst.n2c2.model;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class PatientTest {

    @Test
    public void fromXML() {
        File sample = new File(getClass().getResource("/gold-standard/sample.xml").getPath());
        Patient patient = Patient.fromXML(sample);

        assertEquals(Eligibility.NOT_MET, patient.getEligibility(Criterion.ABDOMINAL));
        assertEquals(Eligibility.MET, patient.getEligibility(Criterion.ADVANCE_CAD));
        assertEquals(Eligibility.NOT_MET, patient.getEligibility(Criterion.ALCOHOL_ABUSE));
        assertEquals(Eligibility.MET, patient.getEligibility(Criterion.ASP_FOR_MI));
        assertEquals(Eligibility.NOT_MET, patient.getEligibility(Criterion.CREATININE));
        assertEquals(Eligibility.MET, patient.getEligibility(Criterion.DIETSUPP_2MOS));
        assertEquals(Eligibility.NOT_MET, patient.getEligibility(Criterion.DRUG_ABUSE));
        assertEquals(Eligibility.MET, patient.getEligibility(Criterion.ENGLISH));
        assertEquals(Eligibility.MET, patient.getEligibility(Criterion.HBA1C));
        assertEquals(Eligibility.NOT_MET, patient.getEligibility(Criterion.KETO_1YR));
        assertEquals(Eligibility.MET, patient.getEligibility(Criterion.MAJOR_DIABETES));
        assertEquals(Eligibility.MET, patient.getEligibility(Criterion.MAKES_DECISIONS));
        assertEquals(Eligibility.MET, patient.getEligibility(Criterion.MI_6MOS));
    }
}