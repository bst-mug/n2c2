package at.medunigraz.imi.bst.n2c2.nn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

/**
 * Date iterator refactored from dl4j examples.
 * 
 * @author Markus
 *
 */
public class N2c2PatientIteratorBML extends BaseNNIterator {

	private static final long serialVersionUID = 1L;

	private final WordVectors wordVectors;

	private final int truncateLength;

	private final TokenizerFactory tokenizerFactory;


	/**
	 * Patient data iterator for the n2c2 task.
	 * 
	 * @param patients
	 *            Patient data.
	 * @param wordVectors
	 *            Word vectors object.
	 * @param batchSize
	 *            Mini batch size use for processing.
	 * @param truncateLength
	 *            Maximum length of token sequence.
	 */
	public N2c2PatientIteratorBML(List<Patient> patients, WordVectors wordVectors, int batchSize, int truncateLength) {

		this.patients = patients;
		this.batchSize = batchSize;
		this.vectorSize = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).length;

		this.wordVectors = wordVectors;
		this.truncateLength = truncateLength;

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
				if (wordVectors.hasWord(token))
					tokensFiltered.add(token);
			}
			allTokens.add(tokensFiltered);
			maxLength = Math.max(maxLength, tokensFiltered.size());
		}

		// truncate if sequence is longer than truncateLength
		if (maxLength > truncateLength)
			maxLength = truncateLength;

		INDArray features = Nd4j.create(narratives.size(), vectorSize, maxLength);
		INDArray labels = Nd4j.create(narratives.size(), 13, maxLength);

		INDArray featuresMask = Nd4j.zeros(narratives.size(), maxLength);
		INDArray labelsMask = Nd4j.zeros(narratives.size(), maxLength);

		int[] temp = new int[2];
		for (int i = 0; i < narratives.size(); i++) {
			List<String> tokens = allTokens.get(i);
			temp[0] = i;

			// get word vectors for each token in narrative
			for (int j = 0; j < tokens.size() && j < maxLength; j++) {
				String token = tokens.get(j);
				INDArray vector = wordVectors.getWordVectorMatrix(token);
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
}
