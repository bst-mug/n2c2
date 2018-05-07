package at.medunigraz.imi.bst.n2c2.prediction;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;

import java.io.File;
import java.util.List;

public class Predictor {

    private ClassifierFactory factory;

    public Predictor(ClassifierFactory classifierFactory) {
        this.factory = classifierFactory;
    }

    public void loadTrainPredictSave(File trainingFolder, File testFolder, File predictionFolder) {
        List<Patient> training = DatasetUtil.loadFromFolder(trainingFolder);

        // TODO check if it works without XML tags
        List<Patient> toPredict = DatasetUtil.loadFromFolder(testFolder);

        List<Patient> prediction = factory.trainAndPredict(training, toPredict);

        DatasetUtil.saveToFolder(prediction, predictionFolder);
    }
}
