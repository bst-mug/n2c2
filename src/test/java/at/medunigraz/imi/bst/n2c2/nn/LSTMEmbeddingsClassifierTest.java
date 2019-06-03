package at.medunigraz.imi.bst.n2c2.nn;

public class LSTMEmbeddingsClassifierTest extends BaseNNClassifierTest {

    public LSTMEmbeddingsClassifierTest() {
        this.trainClassifier = new LSTMEmbeddingsClassifier();
        this.testClassifier = new LSTMEmbeddingsClassifier();
    }
}
