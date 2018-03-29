package at.medunigraz.imi.bst.n2c2.nn;

import java.util.ArrayList;
import java.util.List;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import at.medunigraz.imi.bst.n2c2.classifier.Classifier;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

/**
 * BI-LSTM classifier for n2c2 task 2018.
 * 
 * @author Markus
 *
 */
public class BILSTMClassifier implements Classifier {

	// size of mini-batch for training
	private int miniBatchSize = 32;

	// length for truncated backpropagation through time
	private int tbpttLength = 3;

	// total number of training epochs
	private int numEpochs = 1;

	// degine time series length
	private int truncateLength = 64;

	// training data
	private List<Patient> patientExamples;

	// accessing Google word vectors
	private WordVectors wordVectors;

	// tokenizer logic
	private TokenizerFactory tokenizerFactory;

	// location of precalculated vectors
	public static final String WORD_VECTORS_PATH = "C:/Users/Markus/Downloads/GoogleNews-vectors-negative300.bin.gz";

	public BILSTMClassifier(List<Patient> examples) {
		this.patientExamples = examples;

		initializeNetwork();
	}

	private void initializeNetwork() {
		initializeTruncateLength();
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
			List<String> tokens = tokenizerFactory.create(narrative).getTokens();
			List<String> tokensFiltered = new ArrayList<>();
			for (String token : tokens) {
				if (wordVectors.hasWord(token))
					tokensFiltered.add(token);
			}
			allTokens.add(tokensFiltered);
			maxLength = Math.max(maxLength, tokensFiltered.size());
		}
		this.truncateLength = maxLength;
	}

	@Override
	public void train(List<Patient> examples) {

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
