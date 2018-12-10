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
        data.put("MAJ", new MajorityClassifierFactory().trainAndPredict(trainPatients, testPatients));
        data.put("NN", new NNClassifierFactory().trainAndPredict(trainPatients, testPatients));
        data.put("SVM", new SVMClassifierFactory().trainAndPredict(trainPatients, testPatients));
        data.put("RBC", new RuleBasedClassifierFactory().trainAndPredict(trainPatients, testPatients));
        data.put("GT", new FakeClassifierFactory().trainAndPredict(trainPatients, testPatients));

        dataToCsv(data, new File("false-analysis.csv"));
    }

    private static void dataToCsv(Map<String, List<Patient>> data, File file) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(file));

        writeHeader(data.keySet(), writer);

        List<Patient> patients = data.values().iterator().next();   // Get any patient list

        for (Patient p : patients) {
            List<String> cells = new ArrayList<>();
            cells.add(p.getID());

            for (Criterion c : Criterion.classifiableValues()) {
                for (List<Patient> value : data.values()) {
                    Patient predictedPatient = DatasetUtil.findById(p.getID(), value);
                    cells.add(predictedPatient.getEligibility(c).toString());
                }
            }

            writer.writeNext(cells.toArray(new String[0]));
        }

        writer.close();
    }

    private static void writeHeader(Set<String> strategies, CSVWriter writer) {
        List<String> firstLine = new ArrayList<>();
        List<String> secondLine = new ArrayList<>();

        // First column is empty
        firstLine.add("");
        secondLine.add("");

        for (Criterion c : Criterion.classifiableValues()) {
            for (String s : strategies) {
                firstLine.add(c.toString());
                secondLine.add(s);
            }
        }

        writer.writeNext(firstLine.toArray(new String[0]));
        writer.writeNext(secondLine.toArray(new String[0]));
    }
}
