package at.medunigraz.imi.bst.n2c2;

import at.medunigraz.imi.bst.n2c2.classifier.Classifier;
import at.medunigraz.imi.bst.n2c2.classifier.factory.BDTClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.config.Config;
import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.nn.BILSTMC3GClassifier;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Full run of BILSTMC3G with boosted (AdaBoostM1) decision tree (RandomForest)
 * at the end.
 * 
 * @author Markus
 *
 */
public class BILSTMC3GBDTFullRun {

	private static final Logger LOG = LogManager.getLogger();

	public static void main(String[] args) throws IOException {

		// just needed for simulating test data for the moment
        final File patienFolder = new File(Config.NN_SAMPLES_TRAINING);

		// path to persisted BILSTMC3G model
        final String modelPath = Config.NN_MODELS;

		// path for training boosted decision tree (RandomForest)
        final String pathTrainingBDT = Config.NN_SAMPLES_TRAINING_BDT;

		// probabilities persistence of BILSTMC3G in patient XML format
        final String pathTestBDT = Config.NN_SAMPLES_TEST_BDT;

		// path for saving prediction on test patients
        final String pathPredicted = Config.NN_SAMPLES_PREDICTED_BDT;

		ClassifierFactory classifierFactory = new BDTClassifierFactory();

		// simulate test data, put in here real test data
        // FIXME for submission, change to DatasetUtil.loadFromFolder(testFolder)
		List<Patient> patientsTraining = DatasetUtil.loadFromFolder(patienFolder);
		List<Patient> patientsTestBILSTMC3G = DatasetUtil.stripTags(patientsTraining);

		// save back textual information
		Map<String, String> narrativeMap = new HashMap<String, String>();
		patientsTestBILSTMC3G.forEach(p -> narrativeMap.put(p.getID(), p.getText()));

		// set port for monitoring neural networks
		Properties props = System.getProperties();
		props.setProperty("org.deeplearning4j.ui.port", "9001");

		// generate BILSTMC3G output
		BILSTMC3GClassifier biLSTMC3GClassifier = new BILSTMC3GClassifier(modelPath);
        patientsTestBILSTMC3G.forEach(p -> biLSTMC3GClassifier.predictAndOverwrite(p, pathTestBDT));    //1

		// load prediction format
		List<Patient> patientsTrainingBDT = DatasetUtil.loadFromFolder(new File(pathTrainingBDT));
		List<Patient> patientsTestBDT = DatasetUtil.loadFromFolder(new File(pathTestBDT));

		// classify with boosted (AdaBoostM1) decision tree (RandomForest)
		List<Patient> predicted = new ArrayList<Patient>();
		for (Criterion c : Criterion.classifiableValues()) {
			LOG.info("Training and prediction for criterion {}...", c);
			Classifier classifier = classifierFactory.getClassifier(c);

            classifier.train(patientsTrainingBDT);  //2
            predicted = classifier.predict(patientsTestBDT);    //3
		}

		// write back original text
		for (Patient predictedPatient : predicted) {
			String id = predictedPatient.getID();
			predictedPatient.withText(narrativeMap.get(id));
		}

		// write out
		for (Patient p : predicted) {
			new PatientDAO().toXML(p, new File(pathPredicted + p.getID()));
		}
	}
}
