package at.medunigraz.imi.bst.n2c2.nn.iterator;

import java.util.*;

import at.medunigraz.imi.bst.n2c2.nn.input.InputRepresentation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

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
	 * Next data set implementation.
	 * 
	 * @param num
	 *            Mini batch size.
	 * @return DataSet Patients data set.
	 */
	@Override
	public DataSet getNext(int num) {

		HashMap<Integer, ArrayList<Boolean>> binaryMultiHotVectorMap = new HashMap<Integer, ArrayList<Boolean>>();

		// load narrative from patient
		List<String> narratives = new ArrayList<>(num);
		for (int i = 0; i < num && cursor < totalExamples(); i++) {
			String narrative = patients.get(cursor).getText();
			narratives.add(narrative);

			ArrayList<Boolean> binaryMultiHotVector = new ArrayList<Boolean>();
			fillBinaryMultiHotVector(binaryMultiHotVector);

			binaryMultiHotVectorMap.put(i, binaryMultiHotVector);
			cursor++;
		}

		// filter unknown words and tokenize
		List<List<String>> allTokens = new ArrayList<>(narratives.size());
		int maxLength = 0;
		for (String narrative : narratives) {
			List<String> tokens = tokenizerFactory.create(narrative).getTokens();
			List<String> tokensFiltered = new ArrayList<>();
			for (String token : tokens) {
				if (inputRepresentation.hasRepresentation(token))
					tokensFiltered.add(token);
			}
			allTokens.add(tokensFiltered);
			maxLength = Math.max(maxLength, tokensFiltered.size());
		}

		// truncate if sequence is longer than truncateLength
		if (maxLength > getTruncateLength())
			maxLength = getTruncateLength();

		INDArray features = Nd4j.create(new int[] { narratives.size(), inputRepresentation.getVectorSize(), maxLength}, 'f');
		INDArray labels = Nd4j.create(new int[] { narratives.size(), totalOutcomes(), maxLength}, 'f');

		INDArray featuresMask = Nd4j.zeros(narratives.size(), maxLength);
		INDArray labelsMask = Nd4j.zeros(narratives.size(), maxLength);

		int[] temp = new int[2];
		for (int i = 0; i < narratives.size(); i++) {
			List<String> tokens = allTokens.get(i);
			temp[0] = i;

			// get word vectors for each token in narrative
			for (int j = 0; j < tokens.size() && j < maxLength; j++) {
				String token = tokens.get(j);
				INDArray vector = inputRepresentation.getVector(token);
				features.put(new INDArrayIndex[] { NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.point(j) },
						vector);

				temp[1] = j;
				featuresMask.putScalar(temp, 1.0);
			}

			int lastIdx = Math.min(tokens.size(), maxLength);

			// set binary multi-labels
			ArrayList<Boolean> binaryMultiHotVector = binaryMultiHotVectorMap.get(i);
			int labelIndex = 0;
			for (Boolean label : binaryMultiHotVector) {
				labels.putScalar(new int[] { i, labelIndex, lastIdx - 1 }, label == true ? 1.0 : 0.0);
				labelIndex++;
			}
			// out exists at the final step of the sequence
			labelsMask.putScalar(new int[] { i, lastIdx - 1 }, 1.0);
		}
		return new DataSet(features, labels, featuresMask, labelsMask);
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
