package at.medunigraz.imi.bst.n2c2.nn.iterator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.medunigraz.imi.bst.n2c2.nn.DataUtilities;
import at.medunigraz.imi.bst.n2c2.nn.input.InputRepresentation;

import at.medunigraz.imi.bst.n2c2.model.Patient;

/**
 * A sentence iterator.
 * 
 * @author Markus
 */
public class SentenceIterator extends BaseNNIterator {

	private static final long serialVersionUID = 1L;

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
		int maxSentences = 0;
		for (Patient patient : patients) {
			List<String> tmpLines = getUnits(patient.getText());
			maxSentences = Math.max(maxSentences, tmpLines.size());
		}
		return maxSentences;
	}

	protected List<String> getUnits(String text) {
		return DataUtilities.getSentences(text);
	}
}
