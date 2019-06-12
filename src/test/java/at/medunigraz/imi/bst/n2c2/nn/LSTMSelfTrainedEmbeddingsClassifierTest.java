package at.medunigraz.imi.bst.n2c2.nn;

public class LSTMSelfTrainedEmbeddingsClassifierTest extends BaseNNClassifierTest {

    public LSTMSelfTrainedEmbeddingsClassifierTest() {
        this.trainClassifier = new LSTMSelfTrainedEmbeddingsClassifier();
        this.testClassifier = new LSTMSelfTrainedEmbeddingsClassifier();
    }
}
