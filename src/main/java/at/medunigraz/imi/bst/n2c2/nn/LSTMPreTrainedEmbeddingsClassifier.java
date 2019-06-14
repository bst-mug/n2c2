package at.medunigraz.imi.bst.n2c2.nn;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import at.medunigraz.imi.bst.n2c2.nn.architecture.LSTMArchitecture;
import at.medunigraz.imi.bst.n2c2.nn.input.WordEmbedding;
import at.medunigraz.imi.bst.n2c2.nn.iterator.TokenIterator;
import org.nd4j.linalg.factory.Nd4j;

/**
 * LSTM classifier for n2c2 task 2018 refactored from dl4j examples.
 *
 * @author Markus
 *
 */
public class LSTMPreTrainedEmbeddingsClassifier extends BaseNNClassifier {

	/**
	 * n2c2 longest training document has 7597 tokens.
	 */
	private static final int MAX_LENGTH = 7597;

	/**
	 * Location of precalculated vectors, extracted from the huge BioWordVec `.bin` file using `print_vectors.sh`.
	 */
	private static final File PRETRAINED_VECTORS = new File(LSTMPreTrainedEmbeddingsClassifier.class.getClassLoader().getResource("BioWordVec-vectors.vec").getFile());

	@Override
	protected void initializeNetwork() {
		fullSetIterator = new TokenIterator(patientExamples, new WordEmbedding(PRETRAINED_VECTORS), MAX_LENGTH, BATCH_SIZE);
		this.net = new LSTMArchitecture().getNetwork(fullSetIterator.getInputRepresentation().getVectorSize());
	}

	@Override
	public void initializeNetworkFromFile(String pathToModel) throws IOException {
		Properties prop = loadProperties(pathToModel);
		final int truncateLength = Integer.parseInt(prop.getProperty(getModelName() + ".truncateLength"));
		fullSetIterator = new TokenIterator(new WordEmbedding(PRETRAINED_VECTORS), truncateLength, BATCH_SIZE);

		super.initializeNetworkFromFile(pathToModel);
	}

	@Override
	protected String getModelName() {
		return getClass().getSimpleName();
	}
}
