package at.medunigraz.imi.bst.n2c2.nn;

import at.medunigraz.imi.bst.n2c2.nn.architecture.Architecture;
import at.medunigraz.imi.bst.n2c2.nn.architecture.LSTMArchitecture;
import at.medunigraz.imi.bst.n2c2.nn.input.WordEmbedding;
import at.medunigraz.imi.bst.n2c2.nn.iterator.TokenIterator;

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
     * n2c2 longest training document has 7597 tokens.
     */
    private static final int TRUNCATE_LENGTH = 2048;

    private static final Architecture ARCHITECTURE = new LSTMArchitecture();

    /**
     * Location of self-trained fasttext vectors (using `train_embeddings.sh`) and extracted from the `.bin` file using `print_vectors.sh`.
     */
    private static final File SELFTRAINED_VECTORS = new File(LSTMSelfTrainedEmbeddingsClassifier.class.getClassLoader().getResource("self-trained-vectors.vec").getFile());

    public LSTMSelfTrainedEmbeddingsClassifier() {
        super(ARCHITECTURE);
    }

    @Override
    protected void initializeNetwork() {
        fullSetIterator = new TokenIterator(patientExamples, new WordEmbedding(SELFTRAINED_VECTORS), TRUNCATE_LENGTH, BATCH_SIZE);
        this.net = architecture.getNetwork(fullSetIterator.getInputRepresentation().getVectorSize());
    }

    @Override
    public void initializeNetworkFromFile(String pathToModel) throws IOException {
        Properties prop = loadProperties(pathToModel);
        final int truncateLength = Integer.parseInt(prop.getProperty("truncateLength"));
        fullSetIterator = new TokenIterator(new WordEmbedding(SELFTRAINED_VECTORS), truncateLength, BATCH_SIZE);

        super.initializeNetworkFromFile(pathToModel);
    }

    @Override
    protected String getModelName() {
        return toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{truncateLength" + TRUNCATE_LENGTH +
                ",batchSize=" + BATCH_SIZE +
                ",architecture=" + architecture.toString() +
                "}";
    }
}
