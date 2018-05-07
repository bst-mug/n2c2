package at.medunigraz.imi.bst.n2c2.runner;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.RuleBasedClassifierFactory;
import at.medunigraz.imi.bst.n2c2.prediction.Predictor;

import java.io.File;

public class SubmissionRunner {
    public static void main(String[] args) {
        final File trainingFolder = new File("data/train");
        final File testFolder = new File("data/test");
        final File predictionFolder = new File("out");

        final ClassifierFactory factory = new RuleBasedClassifierFactory();

        new Predictor(factory).loadTrainPredictSave(trainingFolder, testFolder, predictionFolder);
    }

}
