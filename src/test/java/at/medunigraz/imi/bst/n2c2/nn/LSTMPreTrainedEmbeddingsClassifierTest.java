package at.medunigraz.imi.bst.n2c2.nn;

public class LSTMPreTrainedEmbeddingsClassifierTest extends BaseNNClassifierTest {

    public LSTMPreTrainedEmbeddingsClassifierTest() {
        this.trainClassifier = new LSTMPreTrainedEmbeddingsClassifier();
        this.testClassifier = new LSTMPreTrainedEmbeddingsClassifier();
    }
}
