package at.medunigraz.imi.bst.n2c2.runner;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.NNClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.SVMClassifierFactory;
import at.medunigraz.imi.bst.n2c2.evaluator.BasicEvaluator;
import at.medunigraz.imi.bst.n2c2.evaluator.Evaluator;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.dataset.SingleFoldValidatedDataset;
import at.medunigraz.imi.bst.n2c2.model.metrics.Metrics;
import at.medunigraz.imi.bst.n2c2.stats.CSVStatsWriter;
import at.medunigraz.imi.bst.n2c2.stats.StatsWriter;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import at.medunigraz.imi.bst.n2c2.validation.TestValidator;
import at.medunigraz.imi.bst.n2c2.validation.Validator;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * SVM cross validation.
 * 
 * @author Michel Oleynik
 *
 */
public class TrainingCurvesRunner {

    public static void main(String[] args) throws IOException {
        final File trainFolder = new File("data/train");
        final File testFolder = new File("data/test");

        List<Patient> trainPatients = DatasetUtil.loadFromFolder(trainFolder);
        List<Patient> testPatients = DatasetUtil.loadFromFolder(testFolder);

        //ClassifierFactory factory = new SVMClassifierFactory();
        ClassifierFactory factory = new NNClassifierFactory();

        //Evaluator evaluator = new OfficialEvaluator();
        Evaluator evaluator = new BasicEvaluator();

        SingleFoldValidatedDataset dataset = new SingleFoldValidatedDataset(trainPatients);

        double[] thresholds = {0.005, 0.01, 0.05, 0.10, 0.20, 0.30, 0.40, 0.50, 0.75, 0.90, 1.00};
        for (double threshold : thresholds) {
            dataset.split(threshold, 0.0, 1 - threshold);
            List<Patient> split = dataset.getTrainingSet();

            // We train on the split and test on all training
            Validator trainValidator = new TestValidator(split, trainPatients, factory, evaluator);
            validateAndWrite(trainValidator, new String[]{factory.getClass().getSimpleName(), "train", String.valueOf(threshold)});

            // We train on the split and test on the test set
            Validator testValidator = new TestValidator(split, testPatients, factory, evaluator);
            validateAndWrite(testValidator, new String[] {factory.getClass().getSimpleName(), "test", String.valueOf(threshold)});
        }
    }

    private static void validateAndWrite(Validator validator, String[] pieces) throws IOException {
        final File statsFolder = new File("stats/curves");
        statsFolder.mkdirs();

        Metrics metrics = validator.validate();

        File csvStatsFile = new File(statsFolder, String.join("-", pieces) + ".csv");
        StatsWriter csvWriter = new CSVStatsWriter(csvStatsFile);
        csvWriter.write(metrics);
        csvWriter.close();
    }
}
