package at.medunigraz.imi.bst.n2c2.classifier.factory;

import java.util.HashMap;
import java.util.Map;

import at.medunigraz.imi.bst.n2c2.classifier.Classifier;
import at.medunigraz.imi.bst.n2c2.classifier.bdt.BDTClassifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;

public class BDTClassifierFactory implements ClassifierFactory {

	private static final Map<Criterion, Classifier> classifierByCriterion = new HashMap<>();

	public BDTClassifierFactory() {
        classifierByCriterion.put(Criterion.MAKES_DECISIONS, new BDTClassifier(Criterion.MAKES_DECISIONS));
        classifierByCriterion.put(Criterion.HBA1C, new BDTClassifier(Criterion.HBA1C));
        classifierByCriterion.put(Criterion.ASP_FOR_MI, new BDTClassifier(Criterion.ASP_FOR_MI));
        classifierByCriterion.put(Criterion.ALCOHOL_ABUSE, new BDTClassifier(Criterion.ALCOHOL_ABUSE));
        classifierByCriterion.put(Criterion.ADVANCED_CAD, new BDTClassifier(Criterion.ADVANCED_CAD));
        classifierByCriterion.put(Criterion.CREATININE, new BDTClassifier(Criterion.CREATININE));
        classifierByCriterion.put(Criterion.ENGLISH, new BDTClassifier(Criterion.ENGLISH));
        classifierByCriterion.put(Criterion.MI_6MOS, new BDTClassifier(Criterion.MI_6MOS));
        classifierByCriterion.put(Criterion.DRUG_ABUSE, new BDTClassifier(Criterion.DRUG_ABUSE));
        classifierByCriterion.put(Criterion.MAJOR_DIABETES, new BDTClassifier(Criterion.MAJOR_DIABETES));
        classifierByCriterion.put(Criterion.KETO_1YR, new BDTClassifier(Criterion.KETO_1YR));
        classifierByCriterion.put(Criterion.ABDOMINAL, new BDTClassifier(Criterion.ABDOMINAL));
        classifierByCriterion.put(Criterion.DIETSUPP_2MOS, new BDTClassifier(Criterion.DIETSUPP_2MOS));
    }

	@Override
	public Classifier getClassifier(Criterion criterion) {
		return classifierByCriterion.get(criterion);
	}
}
