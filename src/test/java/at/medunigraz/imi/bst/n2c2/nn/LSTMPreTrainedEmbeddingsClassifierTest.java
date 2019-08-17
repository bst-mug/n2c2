package at.medunigraz.imi.bst.n2c2.nn;

import org.junit.Ignore;

@Ignore
public class LSTMPreTrainedEmbeddingsClassifierTest extends BaseNNClassifierTest {

    public LSTMPreTrainedEmbeddingsClassifierTest() {
        this.trainClassifier = new LSTMPreTrainedEmbeddingsClassifier();
        this.testClassifier = new LSTMPreTrainedEmbeddingsClassifier();
    }
}
