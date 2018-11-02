package at.medunigraz.imi.bst.n2c2.rules;

import at.medunigraz.imi.bst.n2c2.classifier.CriterionBasedClassifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.rules.criteria.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleBasedClassifier extends CriterionBasedClassifier {

	private static final Map<Criterion, Classifiable> name = new HashMap<>();
	static {
        name.put(Criterion.ABDOMINAL, new Abdominal());
        name.put(Criterion.ADVANCED_CAD, new AdvancedCAD());
        name.put(Criterion.ALCOHOL_ABUSE, new AlcoholAbuse());
        name.put(Criterion.ASP_FOR_MI, new AspForMi());
        name.put(Criterion.CREATININE, new Creatinine());
        name.put(Criterion.DIETSUPP_2MOS, new Dietsupp2mos());
        name.put(Criterion.DRUG_ABUSE, new DrugAbuse());
        name.put(Criterion.ENGLISH, new English());
        name.put(Criterion.HBA1C, new HbA1c());
        name.put(Criterion.KETO_1YR, new Keto1Yr());
        name.put(Criterion.MAJOR_DIABETES, new MajorDiabetes());
        name.put(Criterion.MAKES_DECISIONS, new MakesDecisions());
        name.put(Criterion.MI_6MOS, new MI6Mos());
	}

	public RuleBasedClassifier(Criterion c) {
		super(c);
	}

	@Deprecated
	public void train(List<Patient> examples) {

	}

	@Override
	public Eligibility predict(Patient p) {
		return name.get(criterion).isMet(p);
	}
}
