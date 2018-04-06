package at.medunigraz.imi.bst.n2c2.nn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
public class N2c2PatientIterator implements DataSetIterator {

	private static final long serialVersionUID = 1L;

	private final WordVectors wordVectors;

	private final int batchSize;
	private final int vectorSize;
	private final int truncateLength;

	private int cursor = 0;
	private final TokenizerFactory tokenizerFactory;

	private List<Patient> patients;

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
	 * @throws IOException
	 */
	public N2c2PatientIterator(List<Patient> patients, WordVectors wordVectors, int batchSize, int truncateLength)
			throws IOException {

		this.patients = patients;
		this.batchSize = batchSize;
		this.vectorSize = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).length;

		this.wordVectors = wordVectors;
		this.truncateLength = truncateLength;

		tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#next(int)
	 */
	@Override
	public DataSet next(int num) {
		if (cursor >= patients.size())
			throw new NoSuchElementException();
		try {
			return nextPatientsDataSet(num);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Next data set implementation.
	 * 
	 * @param num
	 *            Mini batch size.
	 * @return DataSet Patients data set.
	 * @throws IOException
	 */
	private DataSet nextPatientsDataSet(int num) throws IOException {

		boolean[] positive = new boolean[num];

		// load narrative from patient
		List<String> narratives = new ArrayList<>(num);
		for (int i = 0; i < num && cursor < totalExamples(); i++) {
			String narrative = patients.get(cursor).getText();
			narratives.add(narrative);

			// training on abdominal criteria
			positive[i] = patients.get(cursor).getEligibility(Criterion.ABDOMINAL).equals(Eligibility.MET);
			cursor++;
		}

		// filter unknown words and tokenize
		List<List<String>> allTokens = new ArrayList<>(narratives.size());
		int maxLength = 0;
		for (String narrative : narratives) {
			String cleaned = narrative.replaceAll("[\r\n]+", " ").replaceAll("\\s+", " ");
			List<String> tokens = tokenizerFactory.create(cleaned).getTokens();
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
		INDArray labels = Nd4j.create(narratives.size(), 2, maxLength);

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

			int idx = (positive[i] ? 0 : 1);
			int lastIdx = Math.min(tokens.size(), maxLength);

			// set positive negative label
			labels.putScalar(new int[] { i, idx, lastIdx - 1 }, 1.0);

			// out exists at the final step of the sequence
			labelsMask.putScalar(new int[] { i, lastIdx - 1 }, 1.0);
		}

		return new DataSet(features, labels, featuresMask, labelsMask);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#totalExamples()
	 */
	@Override
	public int totalExamples() {
		return this.patients.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#inputColumns()
	 */
	@Override
	public int inputColumns() {
		return vectorSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#totalOutcomes()
	 */
	@Override
	public int totalOutcomes() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#reset()
	 */
	@Override
	public void reset() {
		cursor = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.nd4j.linalg.dataset.api.iterator.DataSetIterator#resetSupported()
	 */
	public boolean resetSupported() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.nd4j.linalg.dataset.api.iterator.DataSetIterator#asyncSupported()
	 */
	@Override
	public boolean asyncSupported() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#batch()
	 */
	@Override
	public int batch() {
		return batchSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#cursor()
	 */
	@Override
	public int cursor() {
		return cursor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#numExamples()
	 */
	@Override
	public int numExamples() {
		return totalExamples();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.nd4j.linalg.dataset.api.iterator.DataSetIterator#setPreProcessor(org.
	 * nd4j.linalg.dataset.api.DataSetPreProcessor)
	 */
	@Override
	public void setPreProcessor(DataSetPreProcessor preProcessor) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#getLabels()
	 */
	@Override
	public List<String> getLabels() {
		return Arrays.asList("positive", "negative");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return cursor < numExamples();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public DataSet next() {
		return next(batchSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.nd4j.linalg.dataset.api.iterator.DataSetIterator#getPreProcessor()
	 */
	@Override
	public DataSetPreProcessor getPreProcessor() {
		throw new UnsupportedOperationException("Not implemented");
	}
}
