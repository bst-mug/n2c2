package at.medunigraz.imi.bst.n2c2.runner;

import at.medunigraz.imi.bst.n2c2.classifier.factory.*;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FalseAnalysisRunner {
    public static void main(String[] args) throws IOException {
        List<Patient> trainPatients = DatasetUtil.loadFromFolder(new File("data/train"));
        List<Patient> testPatients = DatasetUtil.loadFromFolder(new File("data/test"));

        Map<String, List<Patient>> data = new LinkedHashMap<>();
        data.put("GT", FactoryProvider.getFakeClassifierFactory().trainAndPredict(trainPatients, testPatients));
        data.put("BASELINE", FactoryProvider.getMajorityFactory().trainAndPredict(trainPatients, testPatients));
        data.put("RBC", FactoryProvider.getRBCFactory().trainAndPredict(trainPatients, testPatients));
        data.put("SVM", FactoryProvider.getSVMFactory().trainAndPredict(trainPatients, testPatients));
        data.put("SELF-LR", FactoryProvider.getSelfTrainedPerceptronFactory().trainAndPredict(trainPatients, testPatients));
        data.put("PRE-LR", FactoryProvider.getPreTrainedPerceptronFactory().trainAndPredict(trainPatients, testPatients));
        data.put("SELF-LSTM", FactoryProvider.getLSTMSelfTrainedFactory().trainAndPredict(trainPatients, testPatients));
        data.put("PRE-LSTM", FactoryProvider.getLSTMPreTrainedFactory().trainAndPredict(trainPatients, testPatients));

        dataToCsv(data, new File("false-analysis.csv"));
    }

    private static void dataToCsv(Map<String, List<Patient>> data, File file) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(file));

        List<Patient> groundTruth = data.remove("GT");
        writeHeader(data.keySet(), writer);

        for (Criterion c : Criterion.classifiableValues()) {
            for (Patient p : groundTruth) {
                List<String> cells = new ArrayList<>();
                cells.add(c + "-" + p.getID());
                Patient truthPatient = DatasetUtil.findById(p.getID(), groundTruth);

                // Loop over strategies
                for (List<Patient> value : data.values()) {
                    Patient predictedPatient = DatasetUtil.findById(p.getID(), value);
                    String assessment = predictedPatient.getEligibility(c) == truthPatient.getEligibility(c) ? "T" : "F";
                    cells.add(assessment);
                }
                writer.writeNext(cells.toArray(new String[0]));
            }
        }
        writer.close();
    }

    private static void writeHeader(Set<String> strategies, CSVWriter writer) {
        List<String> header = new ArrayList<>();

        // First column is empty
        header.add("instance");
        header.addAll(strategies);
        writer.writeNext(header.toArray(new String[0]));
    }
}
