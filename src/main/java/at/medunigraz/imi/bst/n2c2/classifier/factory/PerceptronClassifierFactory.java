package at.medunigraz.imi.bst.n2c2.classifier.factory;

import at.medunigraz.imi.bst.n2c2.classifier.Classifier;
import at.medunigraz.imi.bst.n2c2.classifier.PerceptronClassifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;

import java.util.HashMap;
import java.util.Map;

public class PerceptronClassifierFactory implements ClassifierFactory {

    private static final Map<Criterion, Classifier> classifierByCriterion = new HashMap<>();
    static {
        classifierByCriterion.put(Criterion.MAKES_DECISIONS, new PerceptronClassifier(Criterion.MAKES_DECISIONS));
        classifierByCriterion.put(Criterion.HBA1C, new PerceptronClassifier(Criterion.HBA1C));
        classifierByCriterion.put(Criterion.ASP_FOR_MI, new PerceptronClassifier(Criterion.ASP_FOR_MI));
        classifierByCriterion.put(Criterion.ALCOHOL_ABUSE, new PerceptronClassifier(Criterion.ALCOHOL_ABUSE));
        classifierByCriterion.put(Criterion.ADVANCED_CAD, new PerceptronClassifier(Criterion.ADVANCED_CAD));
        classifierByCriterion.put(Criterion.CREATININE, new PerceptronClassifier(Criterion.CREATININE));
        classifierByCriterion.put(Criterion.ENGLISH, new PerceptronClassifier(Criterion.ENGLISH));
        classifierByCriterion.put(Criterion.MI_6MOS, new PerceptronClassifier(Criterion.MI_6MOS));
        classifierByCriterion.put(Criterion.DRUG_ABUSE, new PerceptronClassifier(Criterion.DRUG_ABUSE));
        classifierByCriterion.put(Criterion.MAJOR_DIABETES, new PerceptronClassifier(Criterion.MAJOR_DIABETES));
        classifierByCriterion.put(Criterion.KETO_1YR, new PerceptronClassifier(Criterion.KETO_1YR));
        classifierByCriterion.put(Criterion.ABDOMINAL, new PerceptronClassifier(Criterion.ABDOMINAL));
        classifierByCriterion.put(Criterion.DIETSUPP_2MOS, new PerceptronClassifier(Criterion.DIETSUPP_2MOS));
    }

    @Override
    public Classifier getClassifier(Criterion criterion) {
        return classifierByCriterion.get(criterion);
    }

}
