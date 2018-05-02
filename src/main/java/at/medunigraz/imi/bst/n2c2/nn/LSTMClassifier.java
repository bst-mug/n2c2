package at.medunigraz.imi.bst.n2c2.nn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingModelSaver;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.saver.InMemoryModelSaver;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxScoreIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.ScoreImprovementEpochTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.earlystopping.trainer.IEarlyStoppingTrainer;
import org.deeplearning4j.eval.EvaluationBinary;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.AdaGrad;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import at.medunigraz.imi.bst.n2c2.classifier.PatientBasedClassifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.model.dataset.SingleFoldValidatedDataset;

/**
 * LSTM classifier for n2c2 task 2018 refactored from dl4j examples.
 * 
 * @author Markus
 *
 */
public class LSTMClassifier extends PatientBasedClassifier {

	// size of mini-batch for training
	private int miniBatchSize = 10;

	// length for truncated backpropagation through time
	private int tbpttLength = 50;

	// total number of training epochs
	private int nEpochs = 50;

	// specifies time series length
	private int truncateLength = 64;

	// Google word vector size
	int vectorSize = 300;

	// accessing Google word vectors
	private WordVectors wordVectors;

	// training data
	private List<Patient> patientExamples;

	// tokenizer logic
	private TokenizerFactory tokenizerFactory;

	// multi layer network
	private MultiLayerNetwork net;

	// location of precalculated vectors
	private String wordVectorsPath = "C:/DataN2c2/google/GoogleNews-vectors-negative300.bin.gz";

	// criterion index
	private Map<Criterion, Integer> criterionIndex = new HashMap<Criterion, Integer>();

	// iterator over full training set
	public N2c2PatientIteratorBML fullSetIterator;

	// training counter
	private int trainCounter = 0;

	// is network trained
	private boolean trained = false;

	// logging
	private static final Logger LOG = LogManager.getLogger();

	public LSTMClassifier() {

		this.wordVectors = WordVectorSerializer.loadStaticModel(new File(wordVectorsPath));
		initializeTokenizer();
		initializeCriterionIndex();
	}

	public LSTMClassifier(String pathToWordVectors) {

		this.wordVectorsPath = pathToWordVectors;
		this.wordVectors = WordVectorSerializer.loadStaticModel(new File(wordVectorsPath));

		initializeTokenizer();
		initializeCriterionIndex();
	}

	public LSTMClassifier(String pathToWordVectors, String modelName) {

		this.wordVectorsPath = pathToWordVectors;
		this.wordVectors = WordVectorSerializer.loadStaticModel(new File(wordVectorsPath));

		initializeTokenizer();
		initializeCriterionIndex();

		// TODO load from persisted variable via properties file
		this.truncateLength = 5305;
		this.initializeNetworkFromFile(modelName);

	}

	public LSTMClassifier(List<Patient> examples, String pathToWordVectors) {

		this.patientExamples = examples;
		this.wordVectorsPath = pathToWordVectors;
		this.wordVectors = WordVectorSerializer.loadStaticModel(new File(wordVectorsPath));

		initializeTokenizer();
		initializeCriterionIndex();
		initializeTruncateLength();
		initializeNetworkBinaryMultiLabel();
		initializeMonitoring();

		LOG.info("Minibatchsize  :\t" + miniBatchSize);
		LOG.info("tbptt length   :\t" + tbpttLength);
		LOG.info("Epochs         :\t" + nEpochs);
		LOG.info("Truncate lenght:\t" + truncateLength);
		LOG.info("Vector size    :\t" + vectorSize);
	}

	public LSTMClassifier(List<Patient> examples) {

		this.patientExamples = examples;
		this.wordVectors = WordVectorSerializer.loadStaticModel(new File(wordVectorsPath));

		initializeTokenizer();
		initializeCriterionIndex();
		initializeTruncateLength();
		initializeNetworkBinaryMultiLabel();
		initializeMonitoring();

		LOG.info("Minibatchsize  :\t" + miniBatchSize);
		LOG.info("tbptt length   :\t" + tbpttLength);
		LOG.info("Epochs         :\t" + nEpochs);
		LOG.info("Truncate lenght:\t" + truncateLength);
		LOG.info("Vector size    :\t" + vectorSize);
	}

	private void initializeCriterionIndex() {
		this.criterionIndex.put(Criterion.ABDOMINAL, 0);
		this.criterionIndex.put(Criterion.ADVANCED_CAD, 1);
		this.criterionIndex.put(Criterion.ALCOHOL_ABUSE, 2);
		this.criterionIndex.put(Criterion.ASP_FOR_MI, 3);
		this.criterionIndex.put(Criterion.CREATININE, 4);
		this.criterionIndex.put(Criterion.DIETSUPP_2MOS, 5);
		this.criterionIndex.put(Criterion.DRUG_ABUSE, 6);
		this.criterionIndex.put(Criterion.ENGLISH, 7);
		this.criterionIndex.put(Criterion.HBA1C, 8);
		this.criterionIndex.put(Criterion.KETO_1YR, 9);
		this.criterionIndex.put(Criterion.MAJOR_DIABETES, 10);
		this.criterionIndex.put(Criterion.MAKES_DECISIONS, 11);
		this.criterionIndex.put(Criterion.MI_6MOS, 12);
	}

	private void initializeTokenizer() {
		tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
	}

	public void initializeNetworkFromFile(String modelFile) {
		try {
			File networkFile = new File(getClass().getResource("/models/" + modelFile).getPath());
			this.net = ModelSerializer.restoreMultiLayerNetwork(networkFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * SIGMOID activation and XENT loss function for binary multi-label
	 * classification.
	 */
	private void initializeNetworkBinaryMultiLabel() {

		// https://deeplearning4j.org/workspaces
		Nd4j.getMemoryManager().setAutoGcWindow(10000);
		// Nd4j.getMemoryManager().togglePeriodicGc(false);

		try {
			fullSetIterator = new N2c2PatientIteratorBML(patientExamples, wordVectors, miniBatchSize, truncateLength);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int lstmLayerSize = 128;
		double l2Regulization = 0.01;
		double adaGradCore = 0.04;
		double adaGradGraves = 0.008;

		// seed for reproducibility
		final int seed = 12345;
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed)
				.updater(AdaGrad.builder().learningRate(adaGradCore).build()).regularization(true).l2(l2Regulization)
				.weightInit(WeightInit.XAVIER).gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
				.gradientNormalizationThreshold(1.0).trainingWorkspaceMode(WorkspaceMode.SEPARATE)
				.inferenceWorkspaceMode(WorkspaceMode.SEPARATE).list()
				.layer(0,
						new GravesLSTM.Builder().nIn(vectorSize).nOut(lstmLayerSize)
								.updater(AdaGrad.builder().learningRate(adaGradGraves).build())
								.activation(Activation.SOFTSIGN).build())
				.layer(1,
						new RnnOutputLayer.Builder().activation(Activation.SIGMOID)
								.lossFunction(LossFunctions.LossFunction.XENT).nIn(lstmLayerSize).nOut(13).build())
				.pretrain(false).backprop(true).build();

		// for truncated backpropagation over time
		// .backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength)
		// .tBPTTBackwardLength(tbpttLength).pretrain(false).backprop(true).build();

		this.net = new MultiLayerNetwork(conf);
		this.net.init();
		this.net.setListeners(new ScoreIterationListener(1));
	}

	private void initializeNetworkBinaryMultiLabelDebug() {

		Nd4j.getMemoryManager().setAutoGcWindow(10000); // https://deeplearning4j.org/workspaces

		try {
			fullSetIterator = new N2c2PatientIteratorBML(patientExamples, wordVectors, miniBatchSize, truncateLength);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Set up network configuration
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(0)
				.updater(Adam.builder().learningRate(2e-2).build()).l2(1e-5).weightInit(WeightInit.XAVIER)
				.gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
				.gradientNormalizationThreshold(1.0).trainingWorkspaceMode(WorkspaceMode.SEPARATE)
				.inferenceWorkspaceMode(WorkspaceMode.SEPARATE) // https://deeplearning4j.org/workspaces
				.list().layer(0, new GravesLSTM.Builder().nIn(vectorSize).nOut(256).activation(Activation.TANH).build())
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

	/**
	 * Initialize monitoring.
	 * 
	 */
	private void initializeMonitoring() {
		// setting monitor
		UIServer uiServer = UIServer.getInstance();

		// Configure where the network information (gradients, score vs. time
		// etc) is to be stored. Here: store in memory.
		// Alternative: new FileStatsStorage(File), for saving and loading later
		StatsStorage statsStorage = new InMemoryStatsStorage();

		// Attach the StatsStorage instance to the UI: this allows the contents
		// of the StatsStorage to be visualized
		uiServer.attach(statsStorage);

		// Then add the StatsListener to collect this information from the
		// network, as it trains
		net.setListeners(new StatsListener(statsStorage));
	}

	/**
	 * Get longest token sequence of all patients with respect to existing word
	 * vector out of Google corpus.
	 * 
	 */
	private void initializeTruncateLength() {

		List<List<String>> allTokens = new ArrayList<>(patientExamples.size());
		int maxLength = 0;
		for (Patient patient : patientExamples) {
			String narrative = patient.getText();
			String cleaned = narrative.replaceAll("[\r\n]+", " ").replaceAll("\\s+", " ");
			List<String> tokens = tokenizerFactory.create(cleaned).getTokens();
			List<String> tokensFiltered = new ArrayList<>();
			for (String token : tokens) {
				if (wordVectors.hasWord(token)) {
					tokensFiltered.add(token);
				} else {
					LOG.info("Word2vec representation missing:\t" + token);
				}
			}
			allTokens.add(tokensFiltered);
			maxLength = Math.max(maxLength, tokensFiltered.size());
		}
		this.truncateLength = maxLength;
	}

	/**
	 * Train with early stopping.
	 * 
	 */
	private void trainWithEarlyStoppingBML() {

		List<Patient> trainingSplit = new ArrayList<Patient>();
		List<Patient> validationSplit = new ArrayList<Patient>();

		SingleFoldValidatedDataset dataset = new SingleFoldValidatedDataset(this.patientExamples);
		dataset.split(0.9, 0.1, 0);

		trainingSplit = dataset.getTrainingSet();
		validationSplit = dataset.getValidationSet();

		N2c2PatientIteratorBML training;
		N2c2PatientIteratorBML validation;

		try {
			training = new N2c2PatientIteratorBML(trainingSplit, wordVectors, miniBatchSize, truncateLength);
			validation = new N2c2PatientIteratorBML(validationSplit, wordVectors, miniBatchSize, truncateLength);

			// early stopping on validation
			EarlyStoppingModelSaver<MultiLayerNetwork> saver = new InMemoryModelSaver<>();
			EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>()
					.epochTerminationConditions(new MaxEpochsTerminationCondition(nEpochs),
							new ScoreImprovementEpochTerminationCondition(10))
					.iterationTerminationConditions(new MaxTimeIterationTerminationCondition(4, TimeUnit.HOURS),
							new MaxScoreIterationTerminationCondition(20))
					.scoreCalculator(new DataSetLossCalculator(validation, true)).modelSaver(saver).build();

			// conduct early stopping training
			IEarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, net, training);
			EarlyStoppingResult result = trainer.fit();

			// set back current model to best model
			this.net = (MultiLayerNetwork) result.getBestModel();

			// log evaluation statistics
			this.logEvaluationStats(training, validation, result);

			// save model and parameters for reloading
			this.saveModel(result);

			trainCounter++;
			trained = true;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Training for binary multi label classifcation.
	 * 
	 * @param examples
	 *            Patient training examples.
	 */
	private void trainFullSetBML() {

		// print the number of parameters in the network (and for each layer)
		Layer[] layers = net.getLayers();
		int totalNumParams = 0;
		for (int i = 0; i < layers.length; i++) {
			int nParams = layers[i].numParams();
			LOG.info("Number of parameters in layer " + i + ": " + nParams);
			totalNumParams += nParams;
		}
		LOG.info("Total number of network parameters: " + totalNumParams);

		int epochCounter = 0;

		EvaluationBinary eb = new EvaluationBinary();
		do {

			EvaluationBinary ebepoch = new EvaluationBinary();

			net.fit(fullSetIterator);
			fullSetIterator.reset();

			LOG.info("Epoch " + epochCounter++ + " complete.");
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

		} while (eb.averageAccuracy() < 0.95);

		// save model and parameters for reloading
		this.saveModel(epochCounter);

		trainCounter++;
		trained = true;
	}

	/**
	 * Save model with properties to separate files.
	 * 
	 * @param result
	 */
	private void saveModel(EarlyStoppingResult result) {

		// save model after n epochs
		try {

			File locationToSave = new File("LSTMW2V_MBL_" + trainCounter + ".zip");
			boolean saveUpdater = true;
			ModelSerializer.writeModel(net, locationToSave, saveUpdater);

			try {
				Properties props = new Properties();
				props.setProperty("LSTMW2V_MBL.bestModelEpoch." + trainCounter,
						new Integer(result.getBestModelEpoch()).toString());
				props.setProperty("LSTMW2V_MBL.truncateLength." + trainCounter, new Integer(truncateLength).toString());
				File f = new File("LSTMW2V_MBL_" + trainCounter + ".properties");
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
	}

	/**
	 * Save model with properties to separate files.
	 * 
	 * @param epoch
	 */
	private void saveModel(int epoch) {

		// save model after n epochs
		try {

			File locationToSave = new File("LSTMW2V_MBL_" + trainCounter + ".zip");
			boolean saveUpdater = true;
			ModelSerializer.writeModel(net, locationToSave, saveUpdater);

			try {
				Properties props = new Properties();
				props.setProperty("LSTMW2V_MBL.bestModelEpoch." + trainCounter, new Integer(epoch).toString());
				props.setProperty("LSTMW2V_MBL.truncateLength." + trainCounter, new Integer(truncateLength).toString());
				File f = new File("LSTMW2V_MBL_" + trainCounter + ".properties");
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
	private INDArray loadFeaturesForNarrative(String reviewContents, int maxLength) {

		List<String> tokens = tokenizerFactory.create(reviewContents).getTokens();
		List<String> tokensFiltered = new ArrayList<>();
		for (String t : tokens) {
			if (wordVectors.hasWord(t))
				tokensFiltered.add(t);
		}
		int outputLength = Math.max(maxLength, tokensFiltered.size());

		INDArray features = Nd4j.create(1, vectorSize, outputLength);

		for (int j = 0; j < tokens.size() && j < maxLength; j++) {
			String token = tokens.get(j);
			INDArray vector = wordVectors.getWordVectorMatrix(token);
			features.put(new INDArrayIndex[] { NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(j) },
					vector);
		}
		return features;
	}

	/**
	 * Log evaluation statistics.
	 * 
	 * @param training
	 *            Taining iterator
	 * @param validation
	 *            Validation iterator
	 * @param result
	 *            EarlyStoppingResult
	 */
	private void logEvaluationStats(N2c2PatientIteratorBML training, N2c2PatientIteratorBML validation,
			EarlyStoppingResult result) {
		// resetting iterators
		training.reset();
		validation.reset();

		// ---------------------------------------------
		// printing out evaluation stats
		// ---------------------------------------------
		LOG.info(System.getProperty("line.separator") + "---------------------------------");
		LOG.info("Termination reason: " + result.getTerminationReason());
		LOG.info("Termination details: " + result.getTerminationDetails());
		LOG.info("Total epochs: " + result.getTotalEpochs());
		LOG.info("Best epoch number: " + result.getBestModelEpoch());
		LOG.info("Score at best epoch: " + result.getBestModelScore());
		LOG.info(System.getProperty("line.separator") + "---------------------------------");

		// training
		EvaluationBinary eb = new EvaluationBinary();
		while (training.hasNext()) {
			DataSet t = training.next();
			INDArray features = t.getFeatureMatrix();
			INDArray lables = t.getLabels();
			INDArray inMask = t.getFeaturesMaskArray();
			INDArray outMask = t.getLabelsMaskArray();
			INDArray predicted = net.output(features, false, inMask, outMask);

			eb.eval(lables, predicted, outMask);
		}
		training.reset();
		LOG.info("TRAINING");
		LOG.info(System.getProperty("line.separator") + eb.stats());

		// validation
		eb = new EvaluationBinary();
		while (validation.hasNext()) {
			DataSet t = validation.next();
			INDArray features = t.getFeatureMatrix();
			INDArray lables = t.getLabels();
			INDArray inMask = t.getFeaturesMaskArray();
			INDArray outMask = t.getLabelsMaskArray();
			INDArray predicted = net.output(features, false, inMask, outMask);

			eb.eval(lables, predicted, outMask);
		}
		validation.reset();
		LOG.info("VALIDATION");
		LOG.info(System.getProperty("line.separator") + eb.stats());
	}

	@Override
	public void train(List<Patient> examples) {
		if (trained == false) {
			this.patientExamples = examples;

			initializeTruncateLength();
			initializeNetworkBinaryMultiLabelDebug();
			initializeMonitoring();

			LOG.info("Minibatchsize  :\t" + miniBatchSize);
			LOG.info("tbptt length   :\t" + tbpttLength);
			LOG.info("Epochs         :\t" + nEpochs);
			LOG.info("Truncate length:\t" + truncateLength);

			// trained = true
			trainFullSetBML();
		}
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

		String patientNarrative = p.getText();

		INDArray features = loadFeaturesForNarrative(patientNarrative, this.truncateLength);
		INDArray networkOutput = net.output(features);

		int timeSeriesLength = networkOutput.size(2);
		INDArray probabilitiesAtLastWord = networkOutput.get(NDArrayIndex.point(0), NDArrayIndex.all(),
				NDArrayIndex.point(timeSeriesLength - 1));

		double probabilityForCriterion = 0.0;
		probabilityForCriterion = probabilitiesAtLastWord.getDouble(criterionIndex.get(c));
		Eligibility eligibility = probabilityForCriterion > 0.5 ? Eligibility.MET : Eligibility.NOT_MET;

		LOG.info("\n\n-------------------------------");
		LOG.info("Patient: " + p.getID());
		LOG.info("Probabilities at last time step for {}", c.name());
		LOG.info("Probability\t" + c.name() + ": " + probabilityForCriterion);
		LOG.info("Eligibility\t" + c.name() + ": " + eligibility.name());

		return eligibility;
	}

	public boolean isTrained() {
		return trained;
	}

	public void setTrained(boolean trained) {
		this.trained = trained;
	}
}
