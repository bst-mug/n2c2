package at.medunigraz.imi.bst.n2c2.nn.iterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.medunigraz.imi.bst.n2c2.nn.DataUtilities;
import at.medunigraz.imi.bst.n2c2.nn.input.InputRepresentation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import at.medunigraz.imi.bst.n2c2.model.Patient;

/**
 * A sentence iterator.
 * 
 * @author Markus
 */
public class SentenceIterator extends BaseNNIterator {

	private static final long serialVersionUID = 1L;

	private Map<Integer, List<String>> patientLines;

	/**
	 * Iterator representing sentences as character 3-grams.
	 * 
	 * @param patients
	 *            List of patients.
	 * @param batchSize
	 *            Minibatch size.
	 */
	public SentenceIterator(List<Patient> patients, InputRepresentation inputRepresentation, int batchSize) {
		super(inputRepresentation);

		this.patients = patients;
		this.batchSize = batchSize;

		this.patientLines = createPatientLines(patients);
		this.truncateLength = calculateMaxSentences(patients);
	}

	/**
	 *
	 * @param inputRepresentation
	 * @param truncateLength
	 * @param batchSize
	 */
	public SentenceIterator(InputRepresentation inputRepresentation, int truncateLength, int batchSize) {
		super(inputRepresentation);

		this.truncateLength = truncateLength;
		this.batchSize = batchSize;
	}

	/**
	 * getting lines from all patients
	 *
	 * @param patients
	 * @return
	 */
	public static Map<Integer, List<String>> createPatientLines(List<Patient> patients) {
		// TODO return List<String>
		Map<Integer, List<String>> integerListMap = new HashMap<Integer, List<String>>();

		int patientIndex = 0;
		for (Patient patient : patients) {
			List<String> tmpLines = DataUtilities.getSentences(patient.getText());
			integerListMap.put(patientIndex++, tmpLines);
		}

		return integerListMap;
	}

	private int calculateMaxSentences(List<Patient> patients) {
		// TODO reuse patientLines?
		int maxSentences = 0;
		for (Patient patient : patients) {
			List<String> tmpLines = DataUtilities.getSentences(patient.getText());
			maxSentences = tmpLines.size() > maxSentences ? tmpLines.size() : maxSentences;
		}
		return maxSentences;
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
			// TODO regenerate sentences and do not depend on patientLines?
			List<String> sentences = patientLines.get(cursor);
			patientBatch.put(i, sentences);

			maxLength = sentences.size() > maxLength ? sentences.size() : maxLength;

			ArrayList<Boolean> binaryMultiHotVector = new ArrayList<Boolean>();
			fillBinaryMultiHotVector(binaryMultiHotVector);

			binaryMultiHotVectorMap.put(i, binaryMultiHotVector);
			cursor++;
		}

		// truncate if sequence is longer than maxSentences
		if (maxLength > getTruncateLength())
			maxLength = getTruncateLength();

		INDArray features = Nd4j.create(new int[] { patientBatch.size(), inputRepresentation.getVectorSize(), maxLength }, 'f');
		INDArray labels = Nd4j.create(new int[] { patientBatch.size(), totalOutcomes(), maxLength }, 'f');

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
				INDArray vector = inputRepresentation.getVector(sentence);
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

	protected List<String> getUnits(String text) {
		return DataUtilities.getSentences(text);
	}
}
