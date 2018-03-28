package at.medunigraz.imi.bst.n2c2.nn;

import java.util.List;

import at.medunigraz.imi.bst.n2c2.classifier.Classifier;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class BILSTMClassifier implements Classifier {

	// size of mini-batch for training
	private int miniBatchSize = 32;

	// length for truncated backpropagation through time
	int tbpttLength = 3;

	// total number of training epochs
	int numEpochs = 1;
	
	public BILSTMClassifier() {
		
	}
	
	private void initializeNetwork() {
		
	}
	

	@Override
	public void train(List<Patient> examples) {

	}

	@Override
	public Eligibility predict(Patient p) {
		return null;
	}
}
