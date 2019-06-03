package at.medunigraz.imi.bst.n2c2.nn;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import at.medunigraz.imi.bst.n2c2.nn.input.WordEmbedding;
import at.medunigraz.imi.bst.n2c2.nn.iterator.N2c2PatientIteratorBML;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import at.medunigraz.imi.bst.n2c2.model.Patient;

/**
 * LSTM classifier for n2c2 task 2018 refactored from dl4j examples.
 *
 * @author Markus
 *
 */
public class LSTMClassifier extends BaseNNClassifier {

	// location of precalculated vectors
	private static final File PRETRAINED_VECTORS = new File(LSTMClassifier.class.getClassLoader().getResource("vectors.vec").getFile());

	// logging
	private static final Logger LOG = LogManager.getLogger();

	@Override
	protected void initializeNetwork() {
		initializeNetworkBinaryMultiLabelDebug();
	}

	private void initializeNetworkBinaryMultiLabelDebug() {

		Nd4j.getMemoryManager().setAutoGcWindow(10000); // https://deeplearning4j.org/workspaces

		fullSetIterator = new N2c2PatientIteratorBML(patientExamples, new WordEmbedding(PRETRAINED_VECTORS), BATCH_SIZE);

		// Set up network configuration
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(0)
				.updater(Adam.builder().learningRate(2e-2).build()).regularization(true).l2(1e-5).weightInit(WeightInit.XAVIER)
				.gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
				.gradientNormalizationThreshold(1.0).trainingWorkspaceMode(WorkspaceMode.SEPARATE)
				.inferenceWorkspaceMode(WorkspaceMode.SEPARATE) // https://deeplearning4j.org/workspaces
				.list().layer(0, new GravesLSTM.Builder().nIn(fullSetIterator.getInputRepresentation().getVectorSize()).nOut(256).activation(Activation.TANH).build())
				.layer(1,
						new RnnOutputLayer.Builder().activation(Activation.SIGMOID)
								.lossFunction(LossFunctions.LossFunction.XENT).nIn(256).nOut(13).build())
				.pretrain(false).backprop(true).build();

		// for truncated backpropagation over time
		// .backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength)
		// .tBPTTBackwardLength(tbpttLength).pretrain(false).backprop(true).build();

		this.net = new MultiLayerNetwork(conf);
		this.net.init();
		this.net.setListeners(new ScoreIterationListener(1));
	}

	@Override
	protected String getModelName() {
		return "LSTMW2V_MBL";
	}

	@Override
	public void initializeNetworkFromFile(String pathToModel) {
		Properties prop = null;
		try {
			prop = loadProperties(pathToModel);
			final int truncateLength = Integer.parseInt(prop.getProperty(getModelName() + ".truncateLength"));
			fullSetIterator = new N2c2PatientIteratorBML(new WordEmbedding(PRETRAINED_VECTORS), truncateLength, BATCH_SIZE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		super.initializeNetworkFromFile(pathToModel);
	}
}
