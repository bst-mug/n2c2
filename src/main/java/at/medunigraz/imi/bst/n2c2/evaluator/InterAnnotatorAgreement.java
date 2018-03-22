package at.medunigraz.imi.bst.n2c2.evaluator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


public class InterAnnotatorAgreement extends AbstractEvaluator {

    private static final Logger LOG = LogManager.getLogger();

    private static final String IAA_SCRIPT = "target/lib/iaa.py";

    /**
     * python iaa.py -t # folder1/ folder2/
     * # is the track number (1 - cohort selection or 2 - ADE)
     * folder1 contains the gold standard annotations
     * folder2 contains the test annotations
     */
    private static final List<String> COMMAND = new ArrayList<>();

    static {
        COMMAND.add("python");
        COMMAND.add(IAA_SCRIPT);
        COMMAND.add("-t");
        COMMAND.add("1");
    }

    private File goldStandard, results;

    private Map<String, Float> accuracyPerCriterion = new TreeMap<>();

    public InterAnnotatorAgreement(File goldStandard, File results) {
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

    @Override
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
        for (String s : output) {
            String[] fields = s.trim().split("\\s+");

            if (fields.length != 2) {
                continue;
            }

            String criterion = fields[0];
            Float accuracy = Float.parseFloat(fields[1]);

            accuracyPerCriterion.put(criterion, accuracy);
        }
    }

    private float getAccuracyByCriterion(String criterion) {
        return accuracyPerCriterion.getOrDefault(criterion, 0f);
    }

    public float getOverallAccuracy() {
        return getAccuracyByCriterion("Overall");
    }

    @Override
    public double getF1() {
        return getOverallAccuracy();
    }
}
