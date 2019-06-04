package at.medunigraz.imi.bst.n2c2.nn.iterator;

import java.util.*;

import at.medunigraz.imi.bst.n2c2.nn.input.InputRepresentation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import at.medunigraz.imi.bst.n2c2.model.Patient;

/**
 * Date iterator refactored from dl4j examples.
 * 
 * @author Markus
 *
 */
public class TokenIterator extends BaseNNIterator {
	private static final Logger LOG = LogManager.getLogger();

	private static final long serialVersionUID = 1L;

	private final TokenizerFactory tokenizerFactory;

	/**
	 * Patient data iterator for the n2c2 task.
	 * 
	 * @param patients
	 *            Patient data.
	 * @param batchSize
	 *            Mini batch size use for processing.
	 */
	public TokenIterator(List<Patient> patients, InputRepresentation inputRepresentation, int batchSize) {
		super(inputRepresentation);

		this.patients = patients;
		this.batchSize = batchSize;

		tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

		initializeTruncateLength();
	}

	/**
	 *
	 * @param inputRepresentation
	 * @param truncateLength
	 * @param batchSize
	 */
	public TokenIterator(InputRepresentation inputRepresentation, int truncateLength, int batchSize) {
		super(inputRepresentation);

		this.truncateLength = truncateLength;
		this.batchSize = batchSize;

		tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
	}

	/**
	 * Get longest token sequence of all patients with respect to existing word
	 * vector out of Google corpus.
	 *
	 */
	private void initializeTruncateLength() {

		// type coverage
		Set<String> corpusTypes = new HashSet<String>();
		Set<String> matchedTypes = new HashSet<String>();

		// token coverage
		int filteredSum = 0;
		int tokenSum = 0;

		List<List<String>> allTokens = new ArrayList<>(patients.size());
		int maxLength = 0;

		for (Patient patient : patients) {
			String narrative = patient.getText();
			String cleaned = narrative.replaceAll("[\r\n]+", " ").replaceAll("\\s+", " ");
			List<String> tokens = tokenizerFactory.create(cleaned).getTokens();
			tokenSum += tokens.size();

			List<String> tokensFiltered = new ArrayList<>();
			for (String token : tokens) {
				corpusTypes.add(token);
				if (inputRepresentation.hasRepresentation(token)) {
					tokensFiltered.add(token);
					matchedTypes.add(token);
				} else {
					LOG.info("Word2vec representation missing:\t" + token);
				}
			}
			allTokens.add(tokensFiltered);
			filteredSum += tokensFiltered.size();

			maxLength = Math.max(maxLength, tokensFiltered.size());
		}

		LOG.info("Matched " + matchedTypes.size() + " types out of " + corpusTypes.size());
		LOG.info("Matched " + filteredSum + " tokens out of " + tokenSum);

		this.truncateLength = maxLength;
	}

	protected List<String> getUnits(String text) {
		List<String> tokens = tokenizerFactory.create(text).getTokens();
		List<String> tokensFiltered = new ArrayList<>();
		for (String t : tokens) {
			if (inputRepresentation.hasRepresentation(t))
				tokensFiltered.add(t);
		}
		return tokensFiltered;
	}
}
