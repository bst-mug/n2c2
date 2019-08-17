package at.medunigraz.imi.bst.n2c2.nn;

import at.medunigraz.imi.bst.n2c2.classifier.PatientBasedClassifier;
import at.medunigraz.imi.bst.n2c2.config.Config;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.nn.architecture.Architecture;
import at.medunigraz.imi.bst.n2c2.nn.iterator.BaseNNIterator;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.eval.EvaluationBinary;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public abstract class BaseNNClassifier extends PatientBasedClassifier {

    private static final Logger LOG = LogManager.getLogger();

    // size of mini-batch for training
    protected static final int BATCH_SIZE = 10;

    protected static final int MAX_EPOCHS = 25;

    // training data
    protected List<Patient> patientExamples;

    // multi layer network
    protected MultiLayerNetwork net;

    public BaseNNIterator fullSetIterator;

    protected final Architecture architecture;

    public BaseNNClassifier(Architecture architecture) {
        this.architecture = architecture;

        // settings for memory management:
        // https://deeplearning4j.org/workspaces

        Nd4j.getMemoryManager().setAutoGcWindow(10000);
        // Nd4j.getMemoryManager().togglePeriodicGc(false);
    }

    /**
     * Training for binary multi label classifcation.
     */
    protected void trainFullSetBML() {

        // print the number of parameters in the network (and for each layer)
        Layer[] layers = net.getLayers();
        int totalNumParams = 0;
        for (int i = 0; i < layers.length; i++) {
            int nParams = layers[i].numParams();
            LOG.info("Number of parameters in layer " + i + ": " + nParams);
            totalNumParams += nParams;
        }
        LOG.info("Total number of network parameters: " + totalNumParams);

        int epochCounter = 1;

        EvaluationBinary eb = new EvaluationBinary();
        do {

            EvaluationBinary ebepoch = new EvaluationBinary();

            net.fit(fullSetIterator);
            fullSetIterator.reset();

            // save model and parameters for reloading
            this.saveModel(epochCounter);

            LOG.info("Epoch " + epochCounter + " complete.");
            LOG.info("Starting FULL SET evaluation:");

            while (fullSetIterator.hasNext()) {
                DataSet t = fullSetIterator.next();
                INDArray features = t.getFeatureMatrix();
                INDArray lables = t.getLabels();
                INDArray inMask = t.getFeaturesMaskArray();
                INDArray outMask = t.getLabelsMaskArray();
                INDArray predicted = net.output(features, false, inMask, outMask);

                ebepoch.eval(lables, predicted, outMask);
                eb = ebepoch;
            }

            fullSetIterator.reset();
            LOG.info(System.getProperty("line.separator") + ebepoch.stats());
            LOG.info("Average accuracy: {}", eb.averageAccuracy());

        } while (eb.averageAccuracy() < 0.99 && epochCounter++ < MAX_EPOCHS);
    }

    /**
     * Initialize monitoring.
     *
     */
    protected void initializeMonitoring() {
        // setting monitor
        UIServer uiServer = UIServer.getInstance();

        // configure where the network information (gradients, score vs. time
        // etc) is to be stored. Here: store in memory.
        // Alternative: new FileStatsStorage(File), for saving and loading later
        StatsStorage statsStorage = new InMemoryStatsStorage();

        // Attach the StatsStorage instance to the UI: this allows the contents
        // of the StatsStorage to be visualized
        uiServer.attach(statsStorage);

        // then add the StatsListener to collect this information from the
        // network, as it trains
        net.setListeners(new StatsListener(statsStorage));
    }

    protected void saveModel(int epoch) {
        File root = getModelDirectory(patientExamples);

        // save model after n epochs
        try {

            File locationToSave = new File(root, getModelName() + "_" + epoch + ".zip");
            boolean saveUpdater = true;
            ModelSerializer.writeModel(net, locationToSave, saveUpdater);

            try {
                Properties props = new Properties();
                props.setProperty("bestModelEpoch", new Integer(epoch).toString());

                // TODO truncateLength does not change each epoch, this could be persisted in saveParams()
                props.setProperty("truncateLength", new Integer(fullSetIterator.getTruncateLength()).toString());

                File f = new File(root, getModelName() + ".properties");
                OutputStream out = new FileOutputStream(f);
                props.store(out, "Best model at epoch");
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fullSetIterator.save(root);
    }

    protected abstract String getModelName();

    /**
     * Apply the NN on a given patient and modify the given instance with the predictions.
     *
     * @param p A given patient.
     * @return A map of MET probabilities for each criterion.
     */
    public Map<Criterion, Double> predict(Patient p) {
        String patientNarrative = p.getText();

        INDArray features = fullSetIterator.loadFeaturesForNarrative(patientNarrative, fullSetIterator.getTruncateLength());
        INDArray networkOutput = net.output(features);

        int timeSeriesLength = networkOutput.size(2);
        INDArray probabilitiesAtLastWord = networkOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(timeSeriesLength - 1));

        Map<Criterion, Double> ret = new HashMap<>();

        LOG.debug("Patient: " + p.getID());
        for (Criterion c : Criterion.classifiableValues()) {
            double probabilityForCriterion = probabilitiesAtLastWord.getDouble(c.getValue());
            ret.put(c, probabilityForCriterion);

            Eligibility eligibility = probabilityForCriterion > 0.5 ? Eligibility.MET : Eligibility.NOT_MET;
            p.withCriterion(c, eligibility);

            LOG.trace("Probabilities at last time step for {}", c.name());
            LOG.trace("Probability\t" + c.name() + ": " + probabilityForCriterion);
            LOG.trace("Eligibility\t" + c.name() + ": " + eligibility.name());
        }

        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * at.medunigraz.imi.bst.n2c2.classifier.Classifier#predict(at.medunigraz.
     * imi.bst.n2c2.model.Patient, at.medunigraz.imi.bst.n2c2.model.Criterion)
     */
    @Override
    public Eligibility predict(Patient p, Criterion c) {
        predict(p);
        return p.getEligibility(c);
    }

    @Override
    public void train(List<Patient> examples) {
        if (isTrained(examples)) {
            try {
                initializeNetworkFromFile(getModelPath(examples));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            this.patientExamples = examples;

            initializeNetwork();
//            initializeMonitoring();

            LOG.info("Minibatchsize  :\t" + BATCH_SIZE);
            LOG.info("Truncate length:\t" + fullSetIterator.getTruncateLength());

            trainFullSetBML();
        }
    }

    protected static String getModelPath(List<Patient> patients) {
        return Config.NN_MODELS + File.separator + DatasetUtil.getChecksum(patients) + File.separator;
    }

    public File getModelDirectory(List<Patient> patients) {
        File modelDir = new File(getModelPath(patients));
        modelDir.mkdirs();
        return modelDir;
    }

    public void deleteModelDir(List<Patient> patients) throws IOException {
        FileUtils.deleteDirectory(getModelDirectory(patients));
    }

    public boolean isTrained(List<Patient> patients) {
        return new File(getModelPath(patients), getModelName() + ".properties").exists();
    }

    public void initializeNetworkFromFile(String pathToModel) throws IOException {
        Properties prop = loadProperties(pathToModel);
        final int bestEpoch = Integer.parseInt(prop.getProperty("bestModelEpoch"));

        // Limit number of epochs
        final int epoch = Math.min(bestEpoch, MAX_EPOCHS);

        File networkFile = new File(pathToModel, getModelName() + "_" + epoch + ".zip");
        this.net = ModelSerializer.restoreMultiLayerNetwork(networkFile);

        fullSetIterator.load(new File(pathToModel));
    }

    /**
     * load a properties file
     *
     * @param pathToModel
     * @return
     * @throws IOException
     */
    protected Properties loadProperties(String pathToModel) throws IOException {
        Properties prop = new Properties();
        InputStream input = new FileInputStream(new File(pathToModel, getModelName() + ".properties"));
        prop.load(input);
        return prop;
    }

    protected abstract void initializeNetwork();
}
