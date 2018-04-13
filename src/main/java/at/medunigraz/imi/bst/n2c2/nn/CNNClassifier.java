package at.medunigraz.imi.bst.n2c2.nn;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingModelSaver;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.saver.InMemoryModelSaver;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculatorCG;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxScoreIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.ScoreImprovementEpochTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingGraphTrainer;
import org.deeplearning4j.earlystopping.trainer.IEarlyStoppingTrainer;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.iterator.CnnSentenceDataSetIterator;
import org.deeplearning4j.iterator.LabeledSentenceProvider;
import org.deeplearning4j.iterator.provider.CollectionLabeledSentenceProvider;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.GlobalPoolingLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.PoolingType;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

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
public class CNNClassifier implements Classifier {

	// size of mini-batch for training
	private int miniBatchSize = 32;

	// length for truncated backpropagation through time
	private int tbpttLength = 50;

	// total number of training epochs
	private int nEpochs = 100;

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
	private ComputationGraph net;

	// location of precalculated vectors
	private String wordVectorsPath = "C:/Users/Markus/Downloads/GoogleNews-vectors-negative300.bin.gz";

	private static final Logger LOG = LogManager.getLogger();

	public CNNClassifier(List<Patient> examples, String pathToWordVectors) {

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

	public CNNClassifier(List<Patient> examples) {

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
	 * Debugging network.
	 */
	private void initializeNetworkDebug() {

		// https://deeplearning4j.org/workspaces
		Nd4j.getMemoryManager().setAutoGcWindow(5000);
		// Nd4j.getMemoryManager().togglePeriodicGc(false);

		// number of feature maps / channels / depth for each CNN layer
		int cnnLayerFeatureMaps = 100;
		PoolingType globalPoolingType = PoolingType.MAX;

		ComputationGraphConfiguration config = new NeuralNetConfiguration.Builder()
				.trainingWorkspaceMode(WorkspaceMode.SINGLE).inferenceWorkspaceMode(WorkspaceMode.SINGLE)
				.weightInit(WeightInit.RELU).activation(Activation.LEAKYRELU)
				.updater(Adam.builder().learningRate(0.01).build()).convolutionMode(ConvolutionMode.Same).l2(0.0001)
				.trainingWorkspaceMode(WorkspaceMode.SEPARATE).inferenceWorkspaceMode(WorkspaceMode.SEPARATE)
				.graphBuilder().addInputs("input")
				.addLayer("cnn3",
						new ConvolutionLayer.Builder().kernelSize(3, vectorSize).stride(1, vectorSize).nIn(1)
								.nOut(cnnLayerFeatureMaps).build(),
						"input")
				.addLayer("cnn4",
						new ConvolutionLayer.Builder().kernelSize(4, vectorSize).stride(1, vectorSize).nIn(1)
								.nOut(cnnLayerFeatureMaps).build(),
						"input")
				.addLayer("cnn5",
						new ConvolutionLayer.Builder().kernelSize(5, vectorSize).stride(1, vectorSize).nIn(1)
								.nOut(cnnLayerFeatureMaps).build(),
						"input")
				.addVertex("merge", new MergeVertex(), "cnn3", "cnn4", "cnn5")
				.addLayer("globalPool",
						new GlobalPoolingLayer.Builder().poolingType(globalPoolingType).dropOut(0.5).build(), "merge")
				.addLayer("out",
						new OutputLayer.Builder().lossFunction(LossFunctions.LossFunction.MCXENT)
								.activation(Activation.SOFTMAX).nIn(3 * cnnLayerFeatureMaps).nOut(2).build(),
						"globalPool")
				.setOutputs("out").build();

		this.net = new ComputationGraph(config);
		this.net.init();
		this.net.setListeners(new ScoreIterationListener(1));

		LOG.info("Number of parameters by layer:");
		for (Layer l : net.getLayers()) {
			LOG.info("\t" + l.conf().getLayer().getLayerName() + "\t" + l.numParams());
		}

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
		List<Patient> combinedSplit = new ArrayList<Patient>();
		List<Patient> testSplit = new ArrayList<Patient>();

		// generate splits (60 20 20)
		getSplits(examples, trainingSplit, validationSplit, testSplit);
		combinedSplit.addAll(trainingSplit);
		combinedSplit.addAll(validationSplit);

		DataSetIterator training;
		DataSetIterator validation;
		DataSetIterator combined;
		DataSetIterator test;

		Random rng = new Random(12345);

		training = getDataSetIterator(trainingSplit, wordVectors, miniBatchSize, truncateLength, rng);
		validation = getDataSetIterator(validationSplit, wordVectors, miniBatchSize, truncateLength, rng);
		combined = getDataSetIterator(combinedSplit, wordVectors, miniBatchSize, truncateLength, rng);
		test = getDataSetIterator(testSplit, wordVectors, miniBatchSize, truncateLength, rng);

		// early stopping on validation
		EarlyStoppingModelSaver<ComputationGraph> saver = new InMemoryModelSaver<>();
		EarlyStoppingConfiguration<ComputationGraph> esConf = new EarlyStoppingConfiguration.Builder<ComputationGraph>()
				.epochTerminationConditions(new MaxEpochsTerminationCondition(40),
						new ScoreImprovementEpochTerminationCondition(5))
				.iterationTerminationConditions(new MaxTimeIterationTerminationCondition(4, TimeUnit.HOURS),
						new MaxScoreIterationTerminationCondition(20))
				.scoreCalculator(new DataSetLossCalculatorCG(test, true)).modelSaver(saver).build();

		// conduct early stopping training
		IEarlyStoppingTrainer trainer = new EarlyStoppingGraphTrainer(esConf, net, combined);
		EarlyStoppingResult result = trainer.fit();

		LOG.info("Termination reason: " + result.getTerminationReason());
		LOG.info("Termination details: " + result.getTerminationDetails());
		LOG.info("Total epochs: " + result.getTotalEpochs());
		LOG.info("Best epoch number: " + result.getBestModelEpoch());
		LOG.info("Score at best epoch: " + result.getBestModelScore());

		// run evaluation on test data
		LOG.info("Printing TEST evaluation measurements");
		Evaluation evaluationTest = net.evaluate(test);
		LOG.info(evaluationTest.stats());

		// run evaluation on validation data
		LOG.info("Printing COMBINED (TRAINING, VALIDATION) evaluation measurements");
		Evaluation evaluationCombined = net.evaluate(combined);
		LOG.info(evaluationCombined.stats());

		// run evaluation on validation data
		LOG.info("Printing VALIDATION evaluation measurements");
		Evaluation evaluationValidation = net.evaluate(validation);
		LOG.info(evaluationValidation.stats());

		// run evaluation on test data
		LOG.info("Printing TRAINING evaluation measurements");
		Evaluation evaluationTraining = net.evaluate(training);
		LOG.info(evaluationTraining.stats());

	}

	private void trainFullSet(List<Patient> examples) {

		List<Patient> trainingSplit = new ArrayList<Patient>();
		List<Patient> validationSplit = new ArrayList<Patient>();
		List<Patient> combinedSplit = new ArrayList<Patient>();
		List<Patient> testSplit = new ArrayList<Patient>();

		// generate splits (60 20 20)
		getSplits(examples, trainingSplit, validationSplit, testSplit);
		combinedSplit.addAll(trainingSplit);
		combinedSplit.addAll(validationSplit);

		DataSetIterator training;
		DataSetIterator validation;
		DataSetIterator combined;
		DataSetIterator test;

		Random rng = new Random(12345);

		training = getDataSetIterator(trainingSplit, wordVectors, miniBatchSize, truncateLength, rng);
		validation = getDataSetIterator(validationSplit, wordVectors, miniBatchSize, truncateLength, rng);
		combined = getDataSetIterator(combinedSplit, wordVectors, miniBatchSize, truncateLength, rng);
		test = getDataSetIterator(testSplit, wordVectors, miniBatchSize, truncateLength, rng);

		for (int i = 0; i < nEpochs; i++) {
			net.fit(combined);
			combined.reset();
			LOG.info("Epoch " + i + " complete.");
			LOG.info("Starting TRAINING evaluation:");

			// run evaluation on combined data
			Evaluation evaluationCombined = net.evaluate(combined);
			LOG.info(evaluationCombined.stats());

			LOG.info("Starting TEST evaluation:");
			// run evaluation on test data
			Evaluation evaluationTest = net.evaluate(test);
			LOG.info(evaluationTest.stats());
			test.reset();
		}
	}

	private static DataSetIterator getDataSetIterator(List<Patient> patients, WordVectors wordVectors,
			int minibatchSize, int maxSentenceLength, Random rng) {

		List<String> narratives = new ArrayList<String>();
		List<String> labels = new ArrayList<String>();

		// getting narratives
		for (Patient patient : patients) {
			narratives.add(patient.getText().replaceAll("[\r\n]+", " ").replaceAll("\\s+", " "));
			labels.add(patient.getEligibility(Criterion.ABDOMINAL).equals(Eligibility.MET) ? "positive" : "negative");
		}

		// CollectionLabeledSentenceProvider(java.util.List<java.lang.String>
		// sentences, java.util.List<java.lang.String> labelsForSentences)
		LabeledSentenceProvider sentenceProvider = new CollectionLabeledSentenceProvider(narratives, labels);

		return new CnnSentenceDataSetIterator.Builder().sentenceProvider(sentenceProvider).wordVectors(wordVectors)
				.minibatchSize(minibatchSize).maxSentenceLength(maxSentenceLength).useNormalizedWordVectors(false)
				.build();
	}

	@Override
	public void train(List<Patient> examples) {
		// trainWithEarlyStopping(examples);
		trainFullSet(examples);
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
