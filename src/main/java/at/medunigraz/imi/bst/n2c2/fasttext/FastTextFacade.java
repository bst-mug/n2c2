package at.medunigraz.imi.bst.n2c2.fasttext;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class FastTextFacade {

    private static final Logger LOG = LogManager.getLogger();

    private static final String LABEL = "__label__";

    private static final int SUCCESS_CODE = 0;

    private static final int EPOCHS = 100;   // default = 5
    private static final double LEARNING_RATE = 0.5;   // default = 0.1
    private static final int DIMENSIONS = 200;  // default = 100

    // TODO receive via parameter to allow cross-validation (hash of input data)
    private static final File ROOT_FOLDER = new File("fasttext");

    private static final File TRAIN_FILE = new File(ROOT_FOLDER, "train.txt");
    private static final File MODEL_FILE = new File(ROOT_FOLDER, "model");
    private static final File TEST_FILE = new File(ROOT_FOLDER, "test.txt");
    private static final File OUTPUT_FILE = new File(ROOT_FOLDER, "output.txt");

    private static final List<String> COMMAND = Arrays.asList("target/lib/fasttext");

    /**
     * Static class.
     */
    private FastTextFacade() {
    }

    /**
     * @param trainData Map of input text -> label
     */
    public static boolean train(Map<String, String> trainData) {
        List<String> command = getTrainCommand(TRAIN_FILE, MODEL_FILE);
        return train(trainData, command);
    }

    public static boolean train(Map<String, String> trainData, File pretrainedVectors) {
        List<String> command = getTrainCommand(TRAIN_FILE, MODEL_FILE, pretrainedVectors);
        return train(trainData, command);
    }

    private static boolean train(Map<String, String> trainData, List<String> command) {
        try {
            writeTrain(trainData, TRAIN_FILE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int exitCode = runCommand(command);
        if (exitCode != SUCCESS_CODE) {
            throw new RuntimeException("Process exited with code " + exitCode);
        }

        return true;
    }

    private static void writeTrain(Map<String, String> trainData, File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        for (Map.Entry<String, String> entry : trainData.entrySet()) {
            // TODO multilabel support
            writer.write(LABEL);
            writer.write(entry.getValue());
            writer.write(" ");
            writer.write(entry.getKey());
            writer.newLine();
        }

        writer.close();
    }

    /**
     * Prefer predict(List<String> texts) as it might have a performance benefit.
     *
     * @param text
     * @return
     */
    public static String predict(String text) {
        return predict(Arrays.asList(text)).get(0);
    }

    public static List<String> predict(List<String> texts) {
        try {
            FileUtils.writeLines(TEST_FILE, texts);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int exitCode = runCommand(getTestCommand(MODEL_FILE, TEST_FILE), OUTPUT_FILE);
        if (exitCode != SUCCESS_CODE) {
            throw new RuntimeException("Process exited with code " + exitCode);
        }

        try {
            return readPredict(OUTPUT_FILE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> readPredict(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));

        return reader.lines()
            .map(line -> line.substring(LABEL.length()))
            .collect(Collectors.toList());
    }

    private static List<String> getTrainCommand(File inputFile, File modelFile) {
        List<String> command = new ArrayList<>(COMMAND);
        command.add("supervised");
        command.add("-input");
        command.add(inputFile.getAbsolutePath());
        command.add("-output");
        command.add(modelFile.getAbsolutePath());
        command.add("-thread");
        command.add("1");   // Make predictions deterministic
        command.add("-epoch");
        command.add(String.valueOf(EPOCHS));
        command.add("-lr");
        command.add(String.format(Locale.ROOT, "%.2f", LEARNING_RATE));
        command.add("-dim");
        command.add(String.valueOf(DIMENSIONS));
        return command;
    }

    private static List<String> getTrainCommand(File inputFile, File modelFile, File pretrainedVectors) {
        List<String> command = getTrainCommand(inputFile, modelFile);
        command.add("-pretrainedVectors");
        command.add(pretrainedVectors.getAbsolutePath());
        return command;
    }

    private static List<String> getTestCommand(File modelFile, File testFile) {
        List<String> command = new ArrayList<>(COMMAND);
        command.add("predict");    // Also "predict-prob"
        command.add(modelFile.getAbsolutePath() + ".bin");
        command.add(testFile.getAbsolutePath());
        // TODO k + th
        return command;
    }

    private static int runCommand(List<String> command) {
        ProcessBuilder pb = new ProcessBuilder(command);
        LOG.debug(String.join(" ", pb.command()));

        pb.redirectErrorStream(true);

        return run(pb);
    }

    private static int runCommand(List<String> command, File outputFile) {
        ProcessBuilder pb = new ProcessBuilder(command);
        LOG.debug(String.join(" ", pb.command()));

        // We don't redirect error stream if we're interested in the outputFile.
        // pb.redirectErrorStream(true);
        pb.redirectOutput(outputFile);

        return run(pb);
    }

    /**
     * @param pb
     * @return
     * @todo Refactor using http://commons.apache.org/proper/commons-exec/
     */
    private static int run(ProcessBuilder pb) {
        Process proc = null;
        try {
            proc = pb.start();
            proc.waitFor(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String output[] = {};
        try {
            output = collectStream(proc.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int exit = proc.exitValue();
        if (exit != SUCCESS_CODE) {
            LOG.error(String.format("Process exited with code %d", exit));
            for (String o : output) {
                LOG.error(o);
            }
        }

        return exit;
    }

    private static String[] collectStream(InputStream is) throws IOException {
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
}
