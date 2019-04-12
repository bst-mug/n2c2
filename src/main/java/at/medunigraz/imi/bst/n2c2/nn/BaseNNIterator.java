package at.medunigraz.imi.bst.n2c2.nn;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.util.List;

public abstract class BaseNNIterator implements DataSetIterator {


    protected List<Patient> patients;
    protected int cursor = 0;

    /**
     * Fill multi-hot vector for mulit label classification.
     *
     * @param binaryMultiHotVector
     */
    protected void fillBinaryMultiHotVector(List<Boolean> binaryMultiHotVector) {

        // <ABDOMINAL met="not met" />
        binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.ABDOMINAL).equals(Eligibility.MET));

        // <ADVANCED-CAD met="met" />
        binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.ADVANCED_CAD).equals(Eligibility.MET));

        // <ALCOHOL-ABUSE met="not met" />
        binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.ALCOHOL_ABUSE).equals(Eligibility.MET));

        // <ASP-FOR-MI met="met" />
        binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.ASP_FOR_MI).equals(Eligibility.MET));

        // <CREATININE met="not met" />
        binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.CREATININE).equals(Eligibility.MET));

        // <DIETSUPP-2MOS met="met" />
        binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.DIETSUPP_2MOS).equals(Eligibility.MET));

        // <DRUG-ABUSE met="not met" />
        binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.DRUG_ABUSE).equals(Eligibility.MET));

        // <ENGLISH met="met" />
        binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.ENGLISH).equals(Eligibility.MET));

        // <HBA1C met="met" />
        binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.HBA1C).equals(Eligibility.MET));

        // <KETO-1YR met="not met" />
        binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.KETO_1YR).equals(Eligibility.MET));

        // <MAJOR-DIABETES met="met" />
        binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.MAJOR_DIABETES).equals(Eligibility.MET));

        // <MAKES-DECISIONS met="met" />
        binaryMultiHotVector
                .add(patients.get(cursor).getEligibility(Criterion.MAKES_DECISIONS).equals(Eligibility.MET));

        // <MI-6MOS met="met" />
        binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.MI_6MOS).equals(Eligibility.MET));
    }
}
