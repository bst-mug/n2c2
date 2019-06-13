package at.medunigraz.imi.bst.n2c2.nn;

import java.io.IOException;
import java.util.Properties;

import at.medunigraz.imi.bst.n2c2.nn.architecture.BiLSTMArchitecture;
import at.medunigraz.imi.bst.n2c2.nn.input.CharacterTrigram;
import at.medunigraz.imi.bst.n2c2.nn.iterator.SentenceIterator;
import org.nd4j.linalg.factory.Nd4j;

/**
 * BI-LSTM classifier for n2c2 task 2018 refactored from dl4j examples.
 *
 * @author Markus
 *
 */
public class BiLSTMCharacterTrigramClassifier extends BaseNNClassifier {

	@Override
	protected void initializeNetwork() {
		fullSetIterator = new SentenceIterator(patientExamples, new CharacterTrigram(SentenceIterator.createPatientLines(patientExamples)), BATCH_SIZE);
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
