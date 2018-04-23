package at.medunigraz.imi.bst.n2c2.nn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
 * A character 3-gram DataSetIterator.
 * 
 * @author Markus
 */
public class NGramIterator implements DataSetIterator {

	private static final long serialVersionUID = 1L;

	private int miniBatchSize;

	public ArrayList<String> characterNGram_3 = new ArrayList<String>();

	public Map<String, Integer> char3GramToIdxMap = new HashMap<String, Integer>();

	public int maxTokens = 0;

	int sentenceIndex = 0;

	private List<Patient> patients;

	private int cursor = 0;

	public int maxSentences = 0;

	Map<Integer, List<String>> patientLines;

	public int vectorSize;

	DataUtilities utilities = new DataUtilities();

	/**
	 * Default constructor.
	 * 
	 */
	public NGramIterator() {
	}

	/**
	 * 
	 * 
	 * @param patients
	 * @param miniBatchSize
	 * @param characterNGram_3
	 * @param char3GramToIdxMap
	 * @throws IOException
	 */
	public NGramIterator(List<Patient> patients, int miniBatchSize, ArrayList<String> characterNGram_3,
			Map<String, Integer> char3GramToIdxMap) throws IOException {

		this.patients = patients;
		this.miniBatchSize = miniBatchSize;

		// getting lines from all patients
		this.patientLines = new HashMap<Integer, List<String>>();

		int patientIndex = 0;
		for (Patient patient : patients) {
			List<String> tmpLines = DataUtilities.getSentences(patient.getText());
			this.maxSentences = tmpLines.size() > maxSentences ? tmpLines.size() : maxSentences;
			this.patientLines.put(patientIndex++, tmpLines);
		}

		// generate char 3 grams
		this.characterNGram_3 = characterNGram_3;
		this.vectorSize = characterNGram_3.size();

		// generate index
		this.char3GramToIdxMap = char3GramToIdxMap;
	}

	/**
	 * Iterator representing sentences as character 3-grams.
	 * 
	 * @param patients
	 *            List of patients.
	 * @param miniBatchSize
	 *            Minibatch size.
	 * @throws IOException
	 */
	public NGramIterator(List<Patient> patients, int miniBatchSize) throws IOException {

		this.patients = patients;
		this.miniBatchSize = miniBatchSize;

		// getting lines from all patients
		this.patientLines = new HashMap<Integer, List<String>>();

		int patientIndex = 0;
		for (Patient patient : patients) {
			List<String> tmpLines = DataUtilities.getSentences(patient.getText());
			this.maxSentences = tmpLines.size() > maxSentences ? tmpLines.size() : maxSentences;
			this.patientLines.put(patientIndex++, tmpLines);
		}

		// generate char 3 grams
		this.fillCharNGramsMaps();

		// generate index
		this.createIndizes();
	}

	/**
	 * Creates index for character 3-grams.
	 */
	private void createIndizes() {

		// store indexes
		for (int i = 0; i < characterNGram_3.size(); i++)
			char3GramToIdxMap.put(characterNGram_3.get(i), i);
	}

	/**
	 * Fills character 3-gram dictionary.
	 * 
	 * @throws IOException
	 */
	private void fillCharNGramsMaps() throws IOException {

		for (Map.Entry<Integer, List<String>> entry : patientLines.entrySet()) {
			for (String line : entry.getValue()) {
				String normalized = utilities.processTextReduced(line);
				String char3Grams = utilities.getChar3GramRepresentation(normalized);

				// process character n-grams
				String[] char3Splits = char3Grams.split("\\s+");

				for (String split : char3Splits) {
					if (!characterNGram_3.contains(split)) {
						characterNGram_3.add(split);
					}
				}
			}

			// adding out of dictionary entries
			characterNGram_3.add("OOD");

			// set vector dimensionality
			vectorSize = characterNGram_3.size();
		}
	}

	/**
	 * Get next training batch.
	 * 
	 * @param num
	 *            Minibatch size.
	 * @return DatSet
	 * @throws IOException
	 */
	public DataSet getNext(int num) throws IOException {

		HashMap<Integer, ArrayList<Boolean>> binaryMultiHotVectorMap = new HashMap<Integer, ArrayList<Boolean>>();

		// load patient batch
		int maxLength = 0;
		Map<Integer, List<String>> patientBatch = new HashMap<Integer, List<String>>(miniBatchSize);
		for (int i = 0; i < num && cursor < totalExamples(); i++) {
			List<String> sentences = patientLines.get(cursor);
			patientBatch.put(i, sentences);

			maxLength = sentences.size() > maxLength ? sentences.size() : maxLength;

			ArrayList<Boolean> binaryMultiHotVector = new ArrayList<Boolean>();
			fillBinaryMultiHotVector(binaryMultiHotVector);

			binaryMultiHotVectorMap.put(i, binaryMultiHotVector);
			cursor++;
		}

		// truncate if sequence is longer than maxSentences
		if (maxLength > maxSentences)
			maxLength = maxSentences;

		INDArray features = Nd4j.create(new int[] { patientBatch.size(), vectorSize, maxLength }, 'f');
		INDArray labels = Nd4j.create(new int[] { patientBatch.size(), 13, maxLength }, 'f');

		INDArray featuresMask = Nd4j.zeros(patientBatch.size(), maxLength);
		INDArray labelsMask = Nd4j.zeros(patientBatch.size(), maxLength);

		int[] temp = new int[2];
		for (int i = 0; i < patientBatch.size(); i++) {
			List<String> sentences = patientBatch.get(i);
			temp[0] = i;

			// get word vectors for each token in narrative
			for (int j = 0; j < sentences.size() && j < maxLength; j++) {
				String sentence = sentences.get(j);

				// get vector presentation of sentence
				INDArray vector = getChar3GramVectorToSentence(sentence);
				features.put(new INDArrayIndex[] { NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.point(j) },
						vector);

				temp[1] = j;
				featuresMask.putScalar(temp, 1.0);
			}

			int lastIdx = Math.min(sentences.size(), maxLength);

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
	 * Sentence will be transformed to a character 3-gram vector.
	 * 
	 * @param sentence
	 *            Sentence which gets vector representation.
	 * @return
	 */
	public INDArray getChar3GramVectorToSentence(String sentence) {

		INDArray featureVector = Nd4j.zeros(vectorSize);
		try {
			String normalized = utilities.processTextReduced(sentence);
			String char3Grams = utilities.getChar3GramRepresentation(normalized);

			// process character n-grams
			String[] char3Splits = char3Grams.split("\\s+");

			for (String split : char3Splits) {
				if (char3GramToIdxMap.get(split) == null) {
					int nGramIndex = char3GramToIdxMap.get("OOD");
					featureVector.putScalar(new int[] { nGramIndex }, 1.0);
				} else {
					int nGramIndex = char3GramToIdxMap.get(split);
					featureVector.putScalar(new int[] { nGramIndex }, 1.0);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return featureVector;
	}

	/**
	 * Fill multi-hot vector for mulit label classification.
	 * 
	 * @param binaryMultiHotVector
	 */
	private void fillBinaryMultiHotVector(List<Boolean> binaryMultiHotVector) {

		// <ABDOMINAL met="not met" />
		binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.ABDOMINAL).equals(Eligibility.MET));

		// <ADVANCED-CAD met="met" />
		binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.ADVANCED_CAD).equals(Eligibility.MET));

		// <ALCOHOL-ABUSE met="not met" />
		binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.ALCOHOL_ABUSE).equals(Eligibility.MET));

		// <ASP-FOR-MI met="met" />
		binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.ASP_FOR_MI).equals(Eligibility.MET));

		// <CREATININE met="not met" />
		binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.CREATININE).equals(Eligibility.MET));

		// <DIETSUPP-2MOS met="met" />
		binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.DIETSUPP_2MOS).equals(Eligibility.MET));

		// <DRUG-ABUSE met="not met" />
		binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.DRUG_ABUSE).equals(Eligibility.MET));

		// <ENGLISH met="met" />
		binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.ENGLISH).equals(Eligibility.MET));

		// <HBA1C met="met" />
		binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.HBA1C).equals(Eligibility.MET));

		// <KETO-1YR met="not met" />
		binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.KETO_1YR).equals(Eligibility.MET));

		// <MAJOR-DIABETES met="met" />
		binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.MAJOR_DIABETES).equals(Eligibility.MET));

		// <MAKES-DECISIONS met="met" />
		binaryMultiHotVector
				.add(patients.get(cursor).getEligibility(Criterion.MAKES_DECISIONS).equals(Eligibility.MET));

		// <MI-6MOS met="met" />
		binaryMultiHotVector.add(patients.get(cursor).getEligibility(Criterion.MI_6MOS).equals(Eligibility.MET));
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
		return 13;
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
		return miniBatchSize;
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
		return Arrays.asList("ABDOMINAL", "ADVANCED-CAD", "ALCOHOL-ABUSE", "ASP-FOR-MI", "CREATININE", "DIETSUPP-2MOS",
				"DRUG-ABUSE", "ENGLISH", "HBA1C", "KETO-1YR", "MAJOR-DIABETES", "MAKES-DECISIONS", "MI-6MOS");
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public DataSet next() {
		return next(miniBatchSize);
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
			return getNext(num);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
