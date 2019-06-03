package at.medunigraz.imi.bst.n2c2.nn;

import java.io.IOException;
import java.util.Properties;

import at.medunigraz.imi.bst.n2c2.nn.architecture.BiLSTMArchitecture;
import at.medunigraz.imi.bst.n2c2.nn.input.CharacterTrigram;
import at.medunigraz.imi.bst.n2c2.nn.iterator.SentenceIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.linalg.factory.Nd4j;

/**
 * BI-LSTM classifier for n2c2 task 2018 refactored from dl4j examples.
 *
 * @author Markus
 *
 */
public class BiLSTMCharacterTrigramClassifier extends BaseNNClassifier {

	private static final Logger LOG = LogManager.getLogger();

	@Override
	public void initializeNetworkFromFile(String pathToModel) {
		// settings for memory management:
		// https://deeplearning4j.org/workspaces

		Nd4j.getMemoryManager().setAutoGcWindow(10000);
		// Nd4j.getMemoryManager().togglePeriodicGc(false);

		// TODO move to iterator.
		Properties prop = null;
		try {
			prop = loadProperties(pathToModel);
			final int truncateLength = Integer.parseInt(prop.getProperty(getModelName() + ".truncateLength"));
			fullSetIterator = new SentenceIterator(new CharacterTrigram(), truncateLength, BATCH_SIZE);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		super.initializeNetworkFromFile(pathToModel);
	}

    protected void initializeNetwork() {
	    initializeNetworkBinaryMultiLabelDeep();
    }

	/**
	 * SIGMOID activation and XENT loss function for binary multi-label
	 * classification.
	 */
	protected void initializeNetworkBinaryMultiLabelDeep() {

		// settings for memory management:
		// https://deeplearning4j.org/workspaces

		Nd4j.getMemoryManager().setAutoGcWindow(10000);
		// Nd4j.getMemoryManager().togglePeriodicGc(false);

		fullSetIterator = new SentenceIterator(patientExamples, new CharacterTrigram(SentenceIterator.createPatientLines(patientExamples)), BATCH_SIZE);

		this.net = new BiLSTMArchitecture().getNetwork(fullSetIterator.getInputRepresentation().getVectorSize());
	}

	protected String getModelName() {
		return "BILSTMC3G_MBL";
	}
}
