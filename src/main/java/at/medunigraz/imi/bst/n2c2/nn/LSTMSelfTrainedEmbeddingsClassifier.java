package at.medunigraz.imi.bst.n2c2.nn;

import at.medunigraz.imi.bst.n2c2.nn.architecture.LSTMArchitecture;
import at.medunigraz.imi.bst.n2c2.nn.input.WordEmbedding;
import at.medunigraz.imi.bst.n2c2.nn.iterator.TokenIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * LSTM classifier for n2c2 task 2018 refactored from dl4j examples.
 *
 * @author Markus
 *
 */
public class LSTMSelfTrainedEmbeddingsClassifier extends BaseNNClassifier {

	/**
	 * Location of precalculated vectors, extracted from the huge BioWordVec `.bin` file using `print_vectors.sh`.
	 */
	private static final File SELFTRAINED_VECTORS = new File(LSTMSelfTrainedEmbeddingsClassifier.class.getClassLoader().getResource("self-trained-vectors.vec").getFile());

	@Override
	protected void initializeNetwork() {
		initializeNetworkBinaryMultiLabelDebug();
	}

	private void initializeNetworkBinaryMultiLabelDebug() {

		Nd4j.getMemoryManager().setAutoGcWindow(10000); // https://deeplearning4j.org/workspaces

		fullSetIterator = new TokenIterator(patientExamples, new WordEmbedding(SELFTRAINED_VECTORS), BATCH_SIZE);

		// Set up network configuration
		this.net = new LSTMArchitecture().getNetwork(fullSetIterator.getInputRepresentation().getVectorSize());
	}

	@Override
	protected String getModelName() {
		return getClass().getSimpleName();
	}

	@Override
	public void initializeNetworkFromFile(String pathToModel) throws IOException {
		Properties prop = loadProperties(pathToModel);
		final int truncateLength = Integer.parseInt(prop.getProperty(getModelName() + ".truncateLength"));
		fullSetIterator = new TokenIterator(new WordEmbedding(SELFTRAINED_VECTORS), truncateLength, BATCH_SIZE);

		super.initializeNetworkFromFile(pathToModel);
	}
}
