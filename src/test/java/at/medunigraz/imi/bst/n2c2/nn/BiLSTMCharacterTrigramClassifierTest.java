package at.medunigraz.imi.bst.n2c2.nn;

public class BiLSTMCharacterTrigramClassifierTest extends BaseNNClassifierTest {

	public BiLSTMCharacterTrigramClassifierTest() {
		this.trainClassifier = new BiLSTMCharacterTrigramClassifier();
		this.testClassifier = new BiLSTMCharacterTrigramClassifier();
	}


}
