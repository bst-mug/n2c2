package at.medunigraz.imi.bst.n2c2.dao;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class PatientDAOTest {

    @Test
    public void fromXML() throws IOException, SAXException {
        File sample = new File(getClass().getResource("/gold-standard/sample.xml").getPath());
        Patient patient = PatientDAO.fromXML(sample);

        assertThat(patient.getText(), containsString("FISHKILL"));
        assertThat(patient.getText(), containsString("aspirin"));

        Assert.assertEquals(Eligibility.NOT_MET, patient.getEligibility(Criterion.ABDOMINAL));
        assertEquals(Eligibility.MET, patient.getEligibility(Criterion.ADVANCED_CAD));
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