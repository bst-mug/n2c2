package at.medunigraz.imi.bst.n2c2.nn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.GravesBidirectionalLSTM;
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
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import at.medunigraz.imi.bst.n2c2.classifier.Classifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

/**
 * BI-LSTM classifier for n2c2 task 2018 refactored from dl4j examples.
 * 
 * @author Markus
 *
 */
public class BILSTMClassifier implements Classifier {

	// size of mini-batch for training
	private int miniBatchSize = 10;

	// length for truncated backpropagation through time
	private int tbpttLength = 50;

	// total number of training epochs
	private int nEpochs = 40;

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
	private String wordVectorsPath = "C:/Users/Markus/Downloads/GoogleNews-vectors-negative300.bin.gz";

	private static final Logger LOG = LogManager.getLogger();

	public BILSTMClassifier(List<Patient> examples, String pathToWordVectors) {

		this.patientExamples = examples;
		this.wordVectorsPath = pathToWordVectors;
		this.wordVectors = WordVectorSerializer.loadStaticModel(new File(wordVectorsPath));

		initializeTokenizer();
		initializeTruncateLength();
		initializeNetworkDebug();
		initializeMonitoring();

		LOG.info("Minibatchsize  :\t" + miniBatchSize);
		LOG.info("tbptt length   :\t" + tbpttLength);
		LOG.info("Epochs         :\t" + nEpochs);
		LOG.info("Truncate lenght:\t" + truncateLength);
		LOG.info("Vector size    :\t" + vectorSize);
	}

	public BILSTMClassifier(List<Patient> examples) {

		this.patientExamples = examples;
		this.wordVectors = WordVectorSerializer.loadStaticModel(new File(wordVectorsPath));

		initializeTokenizer();
		initializeTruncateLength();
		initializeNetworkDebug();
		initializeMonitoring();

		LOG.info("Minibatchsize  :\t" + miniBatchSize);
		LOG.info("tbptt length   :\t" + tbpttLength);
		LOG.info("Epochs         :\t" + nEpochs);
		LOG.info("Truncate lenght:\t" + truncateLength);
		LOG.info("Vector size    :\t" + vectorSize);
	}

	private void initializeTokenizer() {
		tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
	}

	/**
	 * SOFTMAX activation and MCXENT loss function for binary classification.
	 * Not using truncate backpropagation throught time (tbptt) with
	 * GravesBidirectionalLSTM for the moment.
	 */
	private void initializeNetwork() {

		// Set up network configuration
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(Updater.ADAM).adamMeanDecay(0.9)
				.adamVarDecay(0.999).regularization(true).l2(1e-5).weightInit(WeightInit.XAVIER)
				.gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
				.gradientNormalizationThreshold(1.0).learningRate(2e-2).list()
				.layer(0, new GravesLSTM.Builder().nIn(vectorSize).nOut(256).activation(Activation.TANH).build())
				.layer(1,
						new RnnOutputLayer.Builder().activation(Activation.SOFTMAX)
								.lossFunction(LossFunctions.LossFunction.MCXENT).nIn(256).nOut(2).build())
				.pretrain(false).backprop(true).build();

		this.net = new MultiLayerNetwork(conf);
		this.net.init();
		this.net.setListeners(new ScoreIterationListener(1));
	}

	/**
	 * Debugging network.
	 */
	private void initializeNetworkDebug() {

		// https://deeplearning4j.org/workspaces
		// Nd4j.getMemoryManager().setAutoGcWindow(10000);
		Nd4j.getMemoryManager().togglePeriodicGc(false);

		// seed for reproducibility
		final int seed = 0;
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed)
				.updater(Adam.builder().beta1(0.9).beta2(0.999).build()).regularization(true).l2(1e-5)
				.weightInit(WeightInit.XAVIER).gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
				.gradientNormalizationThreshold(1.0).learningRate(2e-2).trainingWorkspaceMode(WorkspaceMode.SEPARATE)
				.inferenceWorkspaceMode(WorkspaceMode.SEPARATE).list()
				.layer(0, new GravesLSTM.Builder().nIn(vectorSize).nOut(256).activation(Activation.TANH).build())
				.layer(1,
						new RnnOutputLayer.Builder().activation(Activation.SOFTMAX)
								.lossFunction(LossFunctions.LossFunction.MCXENT).nIn(256).nOut(2).build())
				.pretrain(false).backprop(true).build();

		this.net = new MultiLayerNetwork(conf);
		this.net.init();
		this.net.setListeners(new ScoreIterationListener(1));
	}

	/**
	 * SOFTMAX activation and MCXENT loss function for binary classification.
	 * Using truncated backpropagation throught time (tbptt) with GravesLSTM for
	 * the moment.
	 */
	private void initializeNetworkTbptt() {

		// initialize network
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT).iterations(1).learningRate(0.1)
				.rmsDecay(0.95).seed(12345).regularization(true).l2(0.001).weightInit(WeightInit.XAVIER)
				.updater(Updater.ADAGRAD).list()
				.layer(0, new GravesLSTM.Builder().nIn(vectorSize).nOut(256).activation(Activation.SOFTSIGN).build())
				.layer(1,
						new RnnOutputLayer.Builder(LossFunction.MCXENT).activation(Activation.SOFTMAX).nIn(256).nOut(2)
								.build())
				.backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength)
				.tBPTTBackwardLength(tbpttLength).pretrain(false).backprop(true).build();

		this.net = new MultiLayerNetwork(conf);
		this.net.init();
		this.net.setListeners(new ScoreIterationListener(1));
	}

	/**
	 * SIGMOID activation and XENT loss function for binary multi-label
	 * classification.
	 */
	private void initializeNetworkBinaryMultiLabel() {

		// initialize network
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().updater(Updater.ADAM).adamMeanDecay(0.9)
				.adamVarDecay(0.999).regularization(true).l2(1e-5).weightInit(WeightInit.XAVIER)
				.gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
				.gradientNormalizationThreshold(1.0).learningRate(2e-2).list()
				.layer(0,
						new GravesBidirectionalLSTM.Builder().nIn(vectorSize).nOut(truncateLength)
								.activation(Activation.TANH).build())
				.layer(1,
						new RnnOutputLayer.Builder().activation(Activation.SIGMOID)
								.lossFunction(LossFunctions.LossFunction.XENT).nIn(truncateLength).nOut(13).build())
				.pretrain(false).backprop(true).build();

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

	private void getSplits(List<Patient> examples, List<Patient> trainingSplit, List<Patient> validationSplit,
			List<Patient> testSplit) {

		// TODO generalize
		// --------------------------------------
		// abdominal 60 split: 77 / 125 - 46 / 75
		// abdominal 20 split: 77 / 125 - 16 / 25
		// abdominal 20 split: 77 / 125 - 15 / 25
		// --------------------------------------
		// abdominal 100 : 77 / 125 - 77 / 25

		int counterPos = 1;
		int counterNeg = 1;

		for (Patient patient : examples) {
			if (patient.getEligibility(Criterion.ABDOMINAL).equals(Eligibility.MET)) {
				if (counterPos > 62) {
					testSplit.add(patient);
				} else if (counterPos > 46) {
					validationSplit.add(patient);
					counterPos++;
				} else {
					trainingSplit.add(patient);
					counterPos++;
				}
			} else {
				if (counterNeg > 100) {
					testSplit.add(patient);
				} else if (counterNeg > 75) {
					validationSplit.add(patient);
					counterNeg++;
				} else {
					trainingSplit.add(patient);
					counterNeg++;
				}
			}
		}
	}

	private void trainWithEarlyStopping(List<Patient> examples) {

		List<Patient> trainingSplit = new ArrayList<Patient>();
		List<Patient> validationSplit = new ArrayList<Patient>();
		List<Patient> testSplit = new ArrayList<Patient>();

		// generate splits (60 20 20)
		getSplits(examples, trainingSplit, validationSplit, testSplit);

		N2c2PatientIterator training;
		N2c2PatientIterator validation;
		N2c2PatientIterator test;

		try {
			training = new N2c2PatientIterator(trainingSplit, wordVectors, miniBatchSize, truncateLength);
			validation = new N2c2PatientIterator(validationSplit, wordVectors, miniBatchSize, truncateLength);
			test = new N2c2PatientIterator(testSplit, wordVectors, miniBatchSize, truncateLength);

			// early stopping on validation
			EarlyStoppingModelSaver<MultiLayerNetwork> saver = new InMemoryModelSaver<>();
			EarlyStoppingConfiguration<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>()
					.epochTerminationConditions(new MaxEpochsTerminationCondition(100),
							new ScoreImprovementEpochTerminationCondition(5))
					.iterationTerminationConditions(new MaxTimeIterationTerminationCondition(4, TimeUnit.HOURS),
							new MaxScoreIterationTerminationCondition(7.5))
					.scoreCalculator(new DataSetLossCalculator(validation, true)).modelSaver(saver).build();

			// conduct early stopping training
			IEarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, net, training);
			EarlyStoppingResult result = trainer.fit();

			LOG.info("Termination reason: " + result.getTerminationReason());
			LOG.info("Termination details: " + result.getTerminationDetails());
			LOG.info("Total epochs: " + result.getTotalEpochs());
			LOG.info("Best epoch number: " + result.getBestModelEpoch());
			LOG.info("Score at best epoch: " + result.getBestModelScore());

			test.reset();
			// run evaluation on test data
			LOG.info("Printing TEST evaluation measurements");
			Evaluation evaluationTest = net.evaluate(test);
			LOG.info(evaluationTest.stats());

			validation.reset();
			// run evaluation on validation data
			LOG.info("Printing VALIDAITON evaluation measurements");
			Evaluation evaluationValidation = net.evaluate(validation);
			LOG.info(evaluationValidation.stats());

			training.reset();
			// run evaluation on test data
			LOG.info("Printing TRAINING evaluation measurements");
			Evaluation evaluationTraining = net.evaluate(training);
			LOG.info(evaluationTraining.stats());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void trainFullSet(List<Patient> examples) {
		// Print the number of parameters in the network (and for each layer)
		Layer[] layers = net.getLayers();
		int totalNumParams = 0;
		for (int i = 0; i < layers.length; i++) {
			int nParams = layers[i].numParams();
			LOG.info("Number of parameters in layer " + i + ": " + nParams);
			totalNumParams += nParams;
		}
		LOG.info("Total number of network parameters: " + totalNumParams);

		// start training
		try {
			N2c2PatientIterator train = new N2c2PatientIterator(examples, wordVectors, miniBatchSize, truncateLength);

			LOG.info("Starting training");
			for (int i = 0; i < nEpochs; i++) {
				net.fit(train);
				train.reset();

				LOG.info("Epoch " + i + " complete. Starting evaluation:");

				// run evaluation on training data (change to test data)
				Evaluation evaluation = net.evaluate(train);
				LOG.info(evaluation.stats());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void train(List<Patient> examples) {
		trainWithEarlyStopping(examples);
	}

	@Override
	public Eligibility predict(Patient p) {
		return null;
	}

	@Override
	public Eligibility predict(Patient p, Criterion c) {
		return null;
	}

	@Override
	public List<Patient> predict(List<Patient> patientList) {
		return null;
	}
}
