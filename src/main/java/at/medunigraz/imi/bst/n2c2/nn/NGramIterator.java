package at.medunigraz.imi.bst.n2c2.nn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import at.medunigraz.imi.bst.n2c2.model.Patient;

/**
 * A character 3-gram DataSetIterator.
 * 
 * @author Markus
 */
public class NGramIterator extends BaseNNIterator {

	private static final long serialVersionUID = 1L;

	public ArrayList<String> characterNGram_3 = new ArrayList<String>();

	public Map<String, Integer> char3GramToIdxMap = new HashMap<String, Integer>();

	public int maxSentences = 0;

	Map<Integer, List<String>> patientLines;

	/**
	 * Default constructor.
	 * 
	 */
	public NGramIterator() {
	}

	/**
	 * Iterator representing sentences as character 3-grams.
	 * 
	 * @param patients
	 *            List of patients.
	 * @param batchSize
	 *            Minibatch size.
	 * @throws IOException
	 */
	public NGramIterator(List<Patient> patients, int batchSize) throws IOException {

		this.patients = patients;
		this.batchSize = batchSize;

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
				String normalized = DataUtilities.processTextReduced(line);
				String char3Grams = DataUtilities.getChar3GramRepresentation(normalized);

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
	 */
	public DataSet getNext(int num) {

		HashMap<Integer, ArrayList<Boolean>> binaryMultiHotVectorMap = new HashMap<Integer, ArrayList<Boolean>>();

		// load patient batch
		int maxLength = 0;
		Map<Integer, List<String>> patientBatch = new HashMap<Integer, List<String>>(batchSize);
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
			String normalized = DataUtilities.processTextReduced(sentence);
			String char3Grams = DataUtilities.getChar3GramRepresentation(normalized);

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
}
