package at.medunigraz.imi.bst.n2c2.nn;

import java.io.IOException;
import java.util.Properties;

import at.medunigraz.imi.bst.n2c2.nn.architecture.BiLSTMArchitecture;
import at.medunigraz.imi.bst.n2c2.nn.input.CharacterTrigram;
import at.medunigraz.imi.bst.n2c2.nn.iterator.SentenceIterator;

/**
 * BI-LSTM classifier for n2c2 task 2018 refactored from dl4j examples.
 *
 * @author Markus
 *
 */
public class BiLSTMCharacterTrigramClassifier extends BaseNNClassifier {

	/**
	 * n2c2 longest training sentence has 840 different character trigrams.
	 */
	private static final int TRUNCATE_LENGTH = 840;

	@Override
	protected void initializeNetwork() {
		fullSetIterator = new SentenceIterator(patientExamples, new CharacterTrigram(SentenceIterator.createPatientLines(patientExamples)), TRUNCATE_LENGTH, BATCH_SIZE);
		this.net = new BiLSTMArchitecture().getNetwork(fullSetIterator.getInputRepresentation().getVectorSize());
	}

	@Override
	public void initializeNetworkFromFile(String pathToModel) throws IOException {
		// TODO move to iterator.
		Properties prop = loadProperties(pathToModel);
		final int truncateLength = Integer.parseInt(prop.getProperty(getModelName() + ".truncateLength"));
		fullSetIterator = new SentenceIterator(new CharacterTrigram(), truncateLength, BATCH_SIZE);

		super.initializeNetworkFromFile(pathToModel);
	}

	protected String getModelName() {
		return getClass().getSimpleName();
	}
}
