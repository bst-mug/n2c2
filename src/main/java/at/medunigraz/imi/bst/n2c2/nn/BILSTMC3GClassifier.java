package at.medunigraz.imi.bst.n2c2.nn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.GravesBidirectionalLSTM;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.conf.preprocessor.FeedForwardToRnnPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.RnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.AdaGrad;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * BI-LSTM classifier for n2c2 task 2018 refactored from dl4j examples.
 *
 * @author Markus
 *
 */
public class BILSTMC3GClassifier extends BaseNNClassifier {

	private static final Logger LOG = LogManager.getLogger();

	public BILSTMC3GClassifier() {

		initializeCriterionIndex();
	}

	public void initializeNetworkFromFile(String pathToModel) {

		// settings for memory management:
		// https://deeplearning4j.org/workspaces

		Nd4j.getMemoryManager().setAutoGcWindow(10000);
		// Nd4j.getMemoryManager().togglePeriodicGc(false);

		// instantiating generator
		fullSetIterator = new NGramIterator();

		try {

			// load a properties file
			Properties prop = new Properties();
			InputStream input = new FileInputStream(new File(pathToModel, "BILSTMC3G_MBL_0.properties"));

			prop.load(input);
			this.truncateLength = Integer.parseInt(prop.getProperty("BILSTMC3G_MBL.truncateLength.0"));

			// read char 3-grams and index
			FileInputStream fis = new FileInputStream(new File(pathToModel, "characterNGram_3_0"));
			ObjectInputStream ois = new ObjectInputStream(fis);
			ArrayList<String> characterNGram_3 = (ArrayList<String>) ois.readObject();

			((NGramIterator)fullSetIterator).characterNGram_3 = characterNGram_3;
			((NGramIterator)fullSetIterator).vectorSize = characterNGram_3.size();
			this.vectorSize = ((NGramIterator)fullSetIterator).vectorSize;

			// read char 3-grams index
			fis = new FileInputStream(new File(pathToModel, "char3GramToIdxMap_0"));
			ois = new ObjectInputStream(fis);
			Map<String, Integer> char3GramToIdxMap_0 = (HashMap<String, Integer>) ois.readObject();
			((NGramIterator)fullSetIterator).char3GramToIdxMap = char3GramToIdxMap_0;

			Nd4j.getRandom().setSeed(12345);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.initializeNetworkFromFile(pathToModel);
	}

    protected void initializeNetwork() {
	    initializeNetworkBinaryMultiLabelDeep();
    }

	/**
	 * SIGMOID activation and XENT loss function for binary multi-label
	 * classification.
	 */
	protected void initializeNetworkBinaryMultiLabelDeep() {

		// settings for memory management:
		// https://deeplearning4j.org/workspaces

		Nd4j.getMemoryManager().setAutoGcWindow(10000);
		// Nd4j.getMemoryManager().togglePeriodicGc(false);

		try {
			fullSetIterator = new NGramIterator(patientExamples, miniBatchSize);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		vectorSize = ((NGramIterator)fullSetIterator).vectorSize;
		truncateLength = ((NGramIterator)fullSetIterator).maxSentences;

		int nOutFF = 150;
		int lstmLayerSize = 128;
		double l2Regulization = 0.01;
		double adaGradCore = 0.04;
		double adaGradDense = 0.01;
		double adaGradGraves = 0.008;

		// seed for reproducibility
		final int seed = 12345;
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed)
				.updater(AdaGrad.builder().learningRate(adaGradCore).build()).regularization(true).l2(l2Regulization)
				.weightInit(WeightInit.XAVIER).gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
				.gradientNormalizationThreshold(1.0).trainingWorkspaceMode(WorkspaceMode.SINGLE)
				.inferenceWorkspaceMode(WorkspaceMode.SINGLE).list()

				.layer(0, new DenseLayer.Builder().activation(Activation.RELU).nIn(vectorSize).nOut(nOutFF)
						.weightInit(WeightInit.RELU).updater(AdaGrad.builder().learningRate(adaGradDense).build())
						.gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
						.gradientNormalizationThreshold(10).build())

				.layer(1, new DenseLayer.Builder().activation(Activation.RELU).nIn(nOutFF).nOut(nOutFF)
						.weightInit(WeightInit.RELU).updater(AdaGrad.builder().learningRate(adaGradDense).build())
						.gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
						.gradientNormalizationThreshold(10).build())

				.layer(2, new DenseLayer.Builder().activation(Activation.RELU).nIn(nOutFF).nOut(nOutFF)
						.weightInit(WeightInit.RELU).updater(AdaGrad.builder().learningRate(adaGradDense).build())
						.gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
						.gradientNormalizationThreshold(10).build())

				.layer(3,
						new GravesBidirectionalLSTM.Builder().nIn(nOutFF).nOut(lstmLayerSize)
								.updater(AdaGrad.builder().learningRate(adaGradGraves).build())
								.activation(Activation.SOFTSIGN).build())

				.layer(4,
						new GravesLSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize)
								.updater(AdaGrad.builder().learningRate(adaGradGraves).build())
								.activation(Activation.SOFTSIGN).build())

				.layer(5, new RnnOutputLayer.Builder().activation(Activation.SIGMOID)
						.lossFunction(LossFunctions.LossFunction.XENT).nIn(lstmLayerSize).nOut(13).build())

				.inputPreProcessor(0, new RnnToFeedForwardPreProcessor())
				.inputPreProcessor(3, new FeedForwardToRnnPreProcessor()).pretrain(false).backprop(true).build();

		// .backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength).tBPTTBackwardLength(tbpttLength)

		this.net = new MultiLayerNetwork(conf);
		this.net.init();
		this.net.setListeners(new ScoreIterationListener(1));

	}

	protected String getModelName() {
		return "BILSTMC3G_MBL";
	}

	protected void saveModel(int epoch) {
		super.saveModel(epoch);

		File root = getModelDirectory(patientExamples);

		try {
			// writing our character n-grams
			FileOutputStream fos = new FileOutputStream(new File(root, "characterNGram_3_" + trainCounter));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(((NGramIterator)fullSetIterator).characterNGram_3);
			oos.flush();
			oos.close();
			fos.close();

			// writing our character n-grams
			fos = new FileOutputStream(new File(root, "char3GramToIdxMap_" + trainCounter));
			oos = new ObjectOutputStream(fos);
			oos.writeObject(((NGramIterator)fullSetIterator).char3GramToIdxMap);
			oos.flush();
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load features from narrative.
	 *
	 * @param reviewContents
	 *            Narrative content.
	 * @param maxLength
	 *            Maximum length of token series length.
	 * @return Time series feature presentation of narrative.
	 */
	protected INDArray loadFeaturesForNarrative(String reviewContents, int maxLength) {

		List<String> sentences = DataUtilities.getSentences(reviewContents);

		int outputLength = Math.min(maxLength, sentences.size());
		INDArray features = Nd4j.create(1, vectorSize, outputLength);

		for (int j = 0; j < sentences.size() && j < outputLength; j++) {
			String sentence = sentences.get(j);
			INDArray vector = ((NGramIterator)fullSetIterator).getChar3GramVectorToSentence(sentence);
			features.put(new INDArrayIndex[] { NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(j) },
					vector);
		}
		return features;
	}


}
