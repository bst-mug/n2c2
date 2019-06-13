package at.medunigraz.imi.bst.n2c2.nn.iterator;

import java.util.*;

import at.medunigraz.imi.bst.n2c2.nn.DataUtilities;
import at.medunigraz.imi.bst.n2c2.nn.input.InputRepresentation;

import at.medunigraz.imi.bst.n2c2.model.Patient;

/**
 * Date iterator refactored from dl4j examples.
 * 
 * @author Markus
 *
 */
public class TokenIterator extends BaseNNIterator {
	
	private static final long serialVersionUID = 1L;

	public TokenIterator(List<Patient> patients, InputRepresentation inputRepresentation, int batchSize) {
		super(patients, inputRepresentation, batchSize);
	}

	/**
	 * Patient data iterator for the n2c2 task.
	 * 
	 * @param patients
	 *            Patient data.
	 * @param batchSize
	 *            Mini batch size use for processing.
	 */
	public TokenIterator(List<Patient> patients, InputRepresentation inputRepresentation, int truncateLength, int batchSize) {
		super(patients, inputRepresentation, truncateLength, batchSize);
	}

	/**
	 *
	 * @param inputRepresentation
	 * @param truncateLength
	 * @param batchSize
	 */
	public TokenIterator(InputRepresentation inputRepresentation, int truncateLength, int batchSize) {
		super(inputRepresentation, truncateLength, batchSize);
	}

	protected List<String> getUnits(String text) {
		List<String> tokens = DataUtilities.getTokens(text);
		List<String> tokensFiltered = new ArrayList<>();
		for (String t : tokens) {
			if (inputRepresentation.hasRepresentation(t))
				tokensFiltered.add(t);
		}
		return tokensFiltered;
	}
}
