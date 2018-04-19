package at.medunigraz.imi.bst.n2c2.evaluator;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.metrics.MetricSet;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class OfficialEvaluator implements Evaluator {

    private static final Logger LOG = LogManager.getLogger();

    private static final int NUM_OUTPUT_FIELDS = 10;

    private static final String IAA_SCRIPT = "target/track1_eval.py";

    /**
     * python iaa.py -t # folder1/ folder2/
     * # is the track number (1 - cohort selection or 2 - ADE)
     * folder1 contains the gold standard annotations
     * folder2 contains the test annotations
     */
    private static final List<String> COMMAND = new ArrayList<>();

    static {
        COMMAND.add("python3");
        COMMAND.add(IAA_SCRIPT);
    }

    private File goldStandard, results;

    private MetricSet metrics = new MetricSet();

    public OfficialEvaluator() throws FileNotFoundException {
        if (!scriptExists()) {
            throw new FileNotFoundException(IAA_SCRIPT);
        }
    }

    @Deprecated
    public OfficialEvaluator(File goldStandard, File results) {
        this.goldStandard = goldStandard;
        this.results = results;
        evaluate();
    }

    public static boolean scriptExists() {
        return new File(IAA_SCRIPT).isFile();
    }

    private List<String> getFullCommand() {
        List<String> fullCommand = new ArrayList<>(COMMAND);
        fullCommand.add(goldStandard.getAbsolutePath());
        fullCommand.add(results.getAbsolutePath());
        return fullCommand;
    }

    public void evaluate() {
        ProcessBuilder pb = new ProcessBuilder(getFullCommand());
        LOG.debug(String.join(" ", pb.command()));

        pb.redirectErrorStream(true);


        Process proc = null;
        try {
            proc = pb.start();
            proc.waitFor(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String output[] = {};
        try {
            output = collectStream(proc.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        int exit = proc.exitValue();
        if (exit != 0) {
            LOG.error(String.format("Process exited with code %d", exit));
            for (String o : output) {
                LOG.error(o);
            }
            return;
        }

        parseOutput(output);
    }

    @Override
    public void evaluate(List<Patient> gold, List<Patient> results) {
        try {
            this.goldStandard = createDirAndSave(gold, "gold");
            this.results = createDirAndSave(results, "results");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        evaluate();
    }

    private File createDirAndSave(List<Patient> patients, String prefix) throws IOException {
        File ret = Files.createTempDirectory(prefix).toFile();
        DatasetUtil.saveToFolder(patients, ret);
        return ret;
    }

    private String[] collectStream(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        List<String> list = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            LOG.trace(line);
            list.add(line);
        }

        String[] ret = new String[list.size()];
        return list.toArray(ret);
    }

    private void parseOutput(String[] output) {
        /* Example output:
        ******************************************* TRACK 1 ********************************************
                              ------------ met -------------    ------ not met -------    -- overall ---
                              Prec.   Rec.    Speci.  F(b=1)    Prec.   Rec.    F(b=1)    F(b=1)  AUC
                   Abdominal  0.6167  0.6916  0.7459  0.6520    0.8036  0.7459  0.7736    0.7128  0.7187
                Advanced-cad  0.8235  0.7412  0.7712  0.7802    0.6741  0.7712  0.7194    0.7498  0.7562
               Alcohol-abuse  0.0946  0.7000  0.7590  0.1667    0.9860  0.7590  0.8577    0.5122  0.7295
                  Asp-for-mi  0.9235  0.7870  0.7414  0.8498    0.4674  0.7414  0.5733    0.7115  0.7642
                  Creatinine  0.6159  0.8019  0.7088  0.6967    0.8600  0.7088  0.7771    0.7369  0.7553
               Dietsupp-2mos  0.7417  0.7517  0.7194  0.7467    0.7299  0.7194  0.7246    0.7357  0.7356
                  Drug-abuse  0.1264  0.7333  0.7216  0.2157    0.9801  0.7216  0.8312    0.5235  0.7275
                     English  0.9750  0.7358  0.7826  0.8387    0.2045  0.7826  0.3243    0.5815  0.7592
                       Hba1c  0.6818  0.7353  0.8118  0.7075    0.8483  0.8118  0.8297    0.7686  0.7736
                    Keto-1yr  0.0149  1.0000  0.7700  0.0294    1.0000  0.7700  0.8701    0.4497  0.8850
              Major-diabetes  0.7222  0.7500  0.6591  0.7358    0.6905  0.6591  0.6744    0.7051  0.7045
             Makes-decisions  0.9906  0.7617  0.8182  0.8612    0.1200  0.8182  0.2093    0.5353  0.7900
                     Mi-6mos  0.2840  0.8846  0.7786  0.4299    0.9855  0.7786  0.8699    0.6499  0.8316
                              ------------------------------    ----------------------    --------------
             Overall (micro)  0.6952  0.7546  0.7493  0.7237    0.8012  0.7493  0.7744    0.7490  0.7520
             Overall (macro)  0.5855  0.7749  0.7529  0.5931    0.7192  0.7529  0.6950    0.6440  0.7639

                                                           288 files found
         */

        // TODO consolidate validate and getMetrics() into a single method and drop deprecated methods so this is a pure function.
        metrics = new MetricSet();

        for (int i = 0; i < output.length; i++) {
            String line = output[i];
            LOG.debug(line);

            // Matches two or more consecutive whitespaces
            String[] fields = line.trim().split("\\s{2,}");

            if (fields.length != NUM_OUTPUT_FIELDS) {
                continue;
            }

            String criterionName = fields[0];
            if (!Criterion.isValid(criterionName)) {
                continue;
            }

            Criterion criterion = Criterion.get(criterionName);

            // MET
            metrics.withPrecision(criterion, Eligibility.MET, Double.parseDouble(fields[1]));
            metrics.withRecall(criterion, Eligibility.MET, Double.parseDouble(fields[2]));
            metrics.withSpecificity(criterion, Eligibility.MET, Double.parseDouble(fields[3]));
            metrics.withF1(criterion, Eligibility.MET, Double.parseDouble(fields[4]));

            // NOT MET
            metrics.withPrecision(criterion, Eligibility.NOT_MET, Double.parseDouble(fields[5]));
            metrics.withRecall(criterion, Eligibility.NOT_MET, Double.parseDouble(fields[6]));
            metrics.withF1(criterion, Eligibility.NOT_MET, Double.parseDouble(fields[7]));

            // OVERALL
            metrics.withF1(criterion, Eligibility.OVERALL, Double.parseDouble(fields[8]));
            metrics.withAreaUnderCurve(criterion, Eligibility.OVERALL, Double.parseDouble(fields[9]));
        }
    }

    public MetricSet getMetrics() {
        return metrics;
    }
}
