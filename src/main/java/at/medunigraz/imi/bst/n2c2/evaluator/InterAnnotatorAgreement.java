package at.medunigraz.imi.bst.n2c2.evaluator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class InterAnnotatorAgreement extends AbstractEvaluator {

    private static final Logger LOG = LogManager.getLogger();

    /**
     * python iaa.py -t # folder1/ folder2/
     * # is the track number (1 - cohort selection or 2 - ADE)
     * folder1 contains the gold standard annotations
     * folder2 contains the test annotations
     */
    private static final List<String> COMMAND = new ArrayList<>();

    static {
        COMMAND.add("python");
        COMMAND.add("target/lib/iaa.py");
        COMMAND.add("-t");
        COMMAND.add("1");
    }

    public InterAnnotatorAgreement(File goldStandard, File results) {
        COMMAND.add(goldStandard.getAbsolutePath());
        COMMAND.add(results.getAbsolutePath());
        evaluate();
    }


    @Override
    public void evaluate() {
        ProcessBuilder pb = new ProcessBuilder(COMMAND);
        LOG.debug(String.join(" ", pb.command()));

        pb.redirectErrorStream(true);


        Process proc = null;
        try {
            proc = pb.start();
            proc.waitFor(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String output[] = collectStream(proc.getInputStream());

        int exit = proc.exitValue();
        if (exit != 0) {
            LOG.error(String.format("Process exited with code %d", exit));
            for (String o : output) {
                LOG.error(o);
            }
            return;
        }

        //parseOutput(output);
    }

    private String[] collectStream(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        List<String> list = new ArrayList<>();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                LOG.trace(line);
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] ret = new String[list.size()];
        return list.toArray(ret);
    }

    @Override
    public double getF1() {
        // FIXME
        return 0;
    }
}
