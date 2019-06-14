package at.medunigraz.imi.bst.n2c2.classifier.factory;

import at.medunigraz.imi.bst.n2c2.classifier.svm.SVMClassifier;
import at.medunigraz.imi.bst.n2c2.config.Config;
import at.medunigraz.imi.bst.n2c2.model.Criterion;

public class SVMClassifierFactory extends CriterionBasedClassifierFactory {

    public SVMClassifierFactory() {
        classifierByCriterion.put(Criterion.MAKES_DECISIONS, new SVMClassifier(Criterion.MAKES_DECISIONS, Config.SVM_COST_MAKES_DECISIONS));
        classifierByCriterion.put(Criterion.HBA1C, new SVMClassifier(Criterion.HBA1C, Config.SVM_COST_HBA1C));
        classifierByCriterion.put(Criterion.ASP_FOR_MI, new SVMClassifier(Criterion.ASP_FOR_MI, Config.SVM_COST_ASP_FOR_MI));
        classifierByCriterion.put(Criterion.ALCOHOL_ABUSE, new SVMClassifier(Criterion.ALCOHOL_ABUSE, Config.SVM_COST_ALCOHOL_ABUSE));
        classifierByCriterion.put(Criterion.ADVANCED_CAD, new SVMClassifier(Criterion.ADVANCED_CAD, Config.SVM_COST_ADVANCED_CAD));
        classifierByCriterion.put(Criterion.CREATININE, new SVMClassifier(Criterion.CREATININE, Config.SVM_COST_CREATININE));
        classifierByCriterion.put(Criterion.ENGLISH, new SVMClassifier(Criterion.ENGLISH, Config.SVM_COST_ENGLISH));
        classifierByCriterion.put(Criterion.MI_6MOS, new SVMClassifier(Criterion.MI_6MOS, Config.SVM_COST_MI_6MOS).withMonths(6));
        classifierByCriterion.put(Criterion.DRUG_ABUSE, new SVMClassifier(Criterion.DRUG_ABUSE, Config.SVM_COST_DRUG_ABUSE));
        classifierByCriterion.put(Criterion.MAJOR_DIABETES, new SVMClassifier(Criterion.MAJOR_DIABETES, Config.SVM_COST_MAJOR_DIABETES));
        classifierByCriterion.put(Criterion.KETO_1YR, new SVMClassifier(Criterion.KETO_1YR, Config.SVM_COST_KETO_1YR));
        classifierByCriterion.put(Criterion.ABDOMINAL, new SVMClassifier(Criterion.ABDOMINAL, Config.SVM_COST_ABDOMINAL));

        // 6 months is surprisingly better than 2 months when cross-validated
        classifierByCriterion.put(Criterion.DIETSUPP_2MOS, new SVMClassifier(Criterion.DIETSUPP_2MOS, Config.SVM_COST_DIETSUPP_2MOS).withMonths(6));
    }

    public SVMClassifierFactory(double cost) {
        classifierByCriterion.put(Criterion.MAKES_DECISIONS, new SVMClassifier(Criterion.MAKES_DECISIONS, cost));
        classifierByCriterion.put(Criterion.HBA1C, new SVMClassifier(Criterion.HBA1C, cost));
        classifierByCriterion.put(Criterion.ASP_FOR_MI, new SVMClassifier(Criterion.ASP_FOR_MI, cost));
        classifierByCriterion.put(Criterion.ALCOHOL_ABUSE, new SVMClassifier(Criterion.ALCOHOL_ABUSE, cost));
        classifierByCriterion.put(Criterion.ADVANCED_CAD, new SVMClassifier(Criterion.ADVANCED_CAD, cost));
        classifierByCriterion.put(Criterion.CREATININE, new SVMClassifier(Criterion.CREATININE, cost));
        classifierByCriterion.put(Criterion.ENGLISH, new SVMClassifier(Criterion.ENGLISH, cost));
        classifierByCriterion.put(Criterion.MI_6MOS, new SVMClassifier(Criterion.MI_6MOS, cost).withMonths(6));
        classifierByCriterion.put(Criterion.DRUG_ABUSE, new SVMClassifier(Criterion.DRUG_ABUSE, cost));
        classifierByCriterion.put(Criterion.MAJOR_DIABETES, new SVMClassifier(Criterion.MAJOR_DIABETES, cost));
        classifierByCriterion.put(Criterion.KETO_1YR, new SVMClassifier(Criterion.KETO_1YR, cost));
        classifierByCriterion.put(Criterion.ABDOMINAL, new SVMClassifier(Criterion.ABDOMINAL, cost));

        // 6 months is surprisingly better than 2 months when cross-validated
        classifierByCriterion.put(Criterion.DIETSUPP_2MOS, new SVMClassifier(Criterion.DIETSUPP_2MOS, cost).withMonths(6));
    }

}
