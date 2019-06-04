package at.medunigraz.imi.bst.n2c2.nn.input;

import java.io.File;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * Facade for dl4j's WordVectors
 */
public class WordEmbedding implements InputRepresentation {

    private final WordVectors wordVectors;

    public WordEmbedding(File embeddings) {
        wordVectors = WordVectorSerializer.loadStaticModel(embeddings);
    }

    @Override
    public INDArray getVector(String unit) {
        return wordVectors.getWordVectorMatrix(unit);
    }

    @Override
    public boolean hasRepresentation(String unit) {
        return wordVectors.hasWord(unit);
    }

    @Override
    public int getVectorSize() {
        return wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).length;
    }

    @Override
    public void save(File model) {
        // NOOP While we don't train vectors
    }

    @Override
    public void load(File model) {
        // TODO same as constructor
    }
}
