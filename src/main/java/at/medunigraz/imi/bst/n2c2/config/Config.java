package at.medunigraz.imi.bst.n2c2.config;

import java.util.ResourceBundle;

public class Config {
    private static final ResourceBundle PROPERTIES = ResourceBundle.getBundle("config");

    public static final double SVM_COST = Double.valueOf(getString("SVM_COST"));

    public static String getString(String key) {
        return PROPERTIES.getString(key);
    }
}
