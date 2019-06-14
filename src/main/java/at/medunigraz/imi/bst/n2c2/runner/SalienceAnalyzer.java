package at.medunigraz.imi.bst.n2c2.runner;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.FactoryProvider;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.nn.BaseNNClassifier;
import at.medunigraz.imi.bst.n2c2.nn.DataUtilities;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.*;

import static java.util.stream.Collectors.toMap;

public final class SalienceAnalyzer {

    private static final Logger LOG = LogManager.getLogger();

    /**
     * Threshold for MET.
     */
    private static final float THRESHOLD = 0.1f;
    private static final int TOP_N = 10;

    /**
     * Markers developed in the rule-based approach.
     * Most of the time it is a positive marker and also the minority, but not always.
     */
    private static final Map<Criterion, Eligibility> RBC_MARKER = new HashMap<>();
    static {
        RBC_MARKER.put(Criterion.ABDOMINAL, Eligibility.MET);
        RBC_MARKER.put(Criterion.ADVANCED_CAD, Eligibility.MET);            // Minority = NOT_MET
        RBC_MARKER.put(Criterion.ALCOHOL_ABUSE, Eligibility.MET);
        RBC_MARKER.put(Criterion.ASP_FOR_MI, Eligibility.MET);              // Minority = NOT_MET
        RBC_MARKER.put(Criterion.CREATININE, Eligibility.MET);
        RBC_MARKER.put(Criterion.DIETSUPP_2MOS, Eligibility.MET);           // Minority = NOT_MET
        RBC_MARKER.put(Criterion.DRUG_ABUSE, Eligibility.MET);
        RBC_MARKER.put(Criterion.ENGLISH, Eligibility.NOT_MET);             // Negative marker
        RBC_MARKER.put(Criterion.HBA1C, Eligibility.MET);
        RBC_MARKER.put(Criterion.KETO_1YR, Eligibility.MET);
        RBC_MARKER.put(Criterion.MAJOR_DIABETES, Eligibility.MET);          // Minority = NOT_MET
        RBC_MARKER.put(Criterion.MAKES_DECISIONS, Eligibility.NOT_MET);     // Negative marker
        RBC_MARKER.put(Criterion.MI_6MOS, Eligibility.MET);
    }

    public static void main(String[] args) {
        final File trainFolder = new File("data/train");

        List<Patient> trainPatients = DatasetUtil.loadFromFolder(trainFolder);

        ClassifierFactory factory = FactoryProvider.getLSTMPreTrainedFactory();

        // BaseNNClassifier extends PatientBasedClassifier, so it's a single classifier for any criterion.
        BaseNNClassifier nnClassifier = (BaseNNClassifier) factory.getClassifier(Criterion.ABDOMINAL);

        // NOOP when model has been previously saved
        nnClassifier.train(trainPatients);

        SalienceAnalyzer analyzer = new SalienceAnalyzer();

        Map<Criterion, Map<String, Double>> results = analyzer.buildResults(nnClassifier, trainPatients);
        Map<Criterion, Map<String, Double>> sortedResults = analyzer.filterAndSort(results);
        analyzer.printResults(sortedResults);
    }

    private Map<Criterion, Map<String, Double>> buildResults(BaseNNClassifier classifier, List<Patient> patients) {
        Map<Criterion, Map<String, Double>> ret = new HashMap<>();

        // We save a cache of predicted sentences to avoid predicting the same sentence twice.
        Set<String> predictedSentences = new HashSet<>();

        for (Patient patient : patients) {
            List<String> sentences = DataUtilities.getSentences(patient.getText());
            for (String sentence : sentences) {
                if (sentence.isEmpty()) {
                    continue;
                }

                // Do not predict a single sentence twice.
                if (predictedSentences.contains(sentence)) {
                    continue;
                }
                predictedSentences.add(sentence);

                LOG.debug("Sentence = " + sentence);

                Map<Criterion, Double> predictions = classifier.predict(new Patient().withText(sentence));

                // Save results.
                for (Map.Entry<Criterion, Double> prediction : predictions.entrySet()) {
                    Criterion criterion = prediction.getKey();

                    Map<String, Double> criterionSentences = ret.getOrDefault(criterion, new HashMap<>());
                    criterionSentences.put(sentence, prediction.getValue());

                    ret.put(criterion, criterionSentences);
                }
            }
        }

        return ret;
    }

    private Map<Criterion, Map<String, Double>> filterAndSort(Map<Criterion, Map<String, Double>> results) {
        Map<Criterion, Map<String, Double>> ret = new HashMap<>();

        for (Map.Entry<Criterion, Map<String, Double>> criterionEntry : results.entrySet()) {
            Map<String, Double> criterionResults = criterionEntry.getValue();
            final Criterion criterion = criterionEntry.getKey();

            LinkedHashMap<String, Double> sorted;

            switch (RBC_MARKER.get(criterion)) {
                case MET:
                    sorted = criterionResults.entrySet().stream()
                            .filter(e -> e.getValue() > THRESHOLD)
                            .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
                    break;
                case NOT_MET:
                    sorted = criterionResults.entrySet().stream()
                            .filter(e -> e.getValue() < 1 - THRESHOLD)
                            .sorted(Map.Entry.comparingByValue())
                            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
                    break;
                default:
                    throw new RuntimeException("Marker not set for " + criterionEntry.getKey());
            }

            ret.put(criterionEntry.getKey(), sorted);
        }

        return ret;
    }

    private void printResults(Map<Criterion, Map<String, Double>> results) {
        for (Map.Entry<Criterion, Map<String, Double>> criterionEntry : results.entrySet()) {
            System.out.println("---------------------");
            System.out.println(criterionEntry.getKey());
            System.out.println("---------------------");

            Iterator<Map.Entry<String, Double>> iter = criterionEntry.getValue().entrySet().iterator();
            for (int i = 0; i < TOP_N && iter.hasNext(); i++) {
                Map.Entry<String, Double> entry = iter.next();
                System.out.println(String.format("%.6f", entry.getValue()) + " = " + entry.getKey());
            }
        }
    }
}
