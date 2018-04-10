package at.medunigraz.imi.bst.n2c2.config;

import java.util.ResourceBundle;

public class Config {
    private static final ResourceBundle PROPERTIES = ResourceBundle.getBundle("config");

    public static final double SVM_COST_MAKES_DECISIONS = Double.valueOf(getString("SVM_COST_MAKES_DECISIONS"));
    public static final double SVM_COST_HBA1C = Double.valueOf(getString("SVM_COST_HBA1C"));
    public static final double SVM_COST_ASP_FOR_MI = Double.valueOf(getString("SVM_COST_ASP_FOR_MI"));
    public static final double SVM_COST_ALCOHOL_ABUSE = Double.valueOf(getString("SVM_COST_ALCOHOL_ABUSE"));
    public static final double SVM_COST_ADVANCED_CAD = Double.valueOf(getString("SVM_COST_ADVANCED_CAD"));
    public static final double SVM_COST_CREATININE = Double.valueOf(getString("SVM_COST_CREATININE"));
    public static final double SVM_COST_ENGLISH = Double.valueOf(getString("SVM_COST_ENGLISH"));
    public static final double SVM_COST_MI_6MOS = Double.valueOf(getString("SVM_COST_MI_6MOS"));
    public static final double SVM_COST_DRUG_ABUSE = Double.valueOf(getString("SVM_COST_DRUG_ABUSE"));
    public static final double SVM_COST_MAJOR_DIABETES = Double.valueOf(getString("SVM_COST_MAJOR_DIABETES"));
    public static final double SVM_COST_KETO_1YR = Double.valueOf(getString("SVM_COST_KETO_1YR"));
    public static final double SVM_COST_ABDOMINAL = Double.valueOf(getString("SVM_COST_ABDOMINAL"));
    public static final double SVM_COST_DIETSUPP_2MOS = Double.valueOf(getString("SVM_COST_DIETSUPP_2MOS"));

    public static String getString(String key) {
        return PROPERTIES.getString(key);
    }
}
