package at.medunigraz.imi.bst.n2c2.classifier.bdt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.medunigraz.imi.bst.n2c2.classifier.CriterionBasedClassifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

public class BDTClassifier extends CriterionBasedClassifier {

	private static final Logger LOG = LogManager.getLogger();

	private static final int NUM_ATTRIBUTES = 14;
	private static final int ELIGIBILITY_INDEX = 13;

	private Classifier model;
	private Instances dataset;

	public BDTClassifier(Criterion criterion) {
		super(criterion);
		this.model = initializeModel();
		// this.model = initializeSVMModel();
		reset();
	}

	public void reset() {
		this.dataset = createEmptyDataset();
	}

	private Classifier initializeSVMModel() {

		LibSVM svm = new LibSVM();
		svm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE));

		return svm;
	}

	private Classifier initializeModel() {
		RandomForest forest;
		AdaBoostM1 adaboost;

		String[] optionsRF = new String[2];
		optionsRF[0] = "-I";
		optionsRF[1] = "500";

		String[] optionsAB = new String[2];
		optionsAB[0] = "-I";
		optionsAB[1] = "1000";

		adaboost = new AdaBoostM1();
		forest = new RandomForest();

		// set options
		try {
			forest.setOptions(optionsRF);

			adaboost.setClassifier(forest);
			adaboost.setOptions(optionsAB);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return adaboost;
	}

	private List<Attribute> initializeAttributes() {

		List<Attribute> attributes = new ArrayList<>();
		Arrays.stream(Criterion.classifiableValues()).forEach(c -> attributes.add(new Attribute(c.name())));

		List<String> classes = new ArrayList<String>();
		Arrays.stream(Eligibility.classifiableValues()).forEach(e -> classes.add(e.name()));
		attributes.add(new Attribute("ELIGIBILITY", classes));

		return attributes;
	}

	@Override
	public void train(List<Patient> examples) {
		reset();

		examples.forEach(p -> dataset.add(createTrainingInstance(p)));
		LOG.debug("Training boosted (AdaBoostM1) decision tree (Random Forest) with {} patients...", examples.size());

		try {
			model.buildClassifier(dataset);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create initial empty data set.
	 * 
	 * @return Instances
	 */
	private Instances createEmptyDataset() {
		ArrayList<Attribute> attributes = (ArrayList<Attribute>) initializeAttributes();

		Instances data = new Instances(this.getClass().getName(), attributes, 1);
		data.setClassIndex(ELIGIBILITY_INDEX);

		return data;
	}

	private Instance createTrainingInstance(Patient p) {
		Instance instance = createInstance(p, this.dataset);

		return instance;
	}

	private Instance createTestInstance(Patient p) {

		// each test instance has its own, empty, dataset to avoid peeking
		Instances testDataset = createEmptyDataset();
		return createInstance(p, testDataset);
	}

	private Instance createInstance(Patient p, Instances dataset) {
		Instance instance = new DenseInstance(NUM_ATTRIBUTES);
		instance.setDataset(dataset);

		// get probabilities values from deep learned patient
		String[] probabilityValues = p.getText().split("\\s");
		int idx = 0;
		for (String probabilityValue : probabilityValues) {
			instance.setValue(idx++, Double.parseDouble(probabilityValue));
		}

		// depending on training, test
		if (p.hasEligibility(criterion)) {
			instance.setValue(idx, p.getEligibility(this.criterion).name());
		}

		return instance;
	}

	@Override
	public Eligibility predict(Patient p) {
		if (this.dataset.size() == 0) {
			throw new UnsupportedOperationException("Dataset is empty. Check whether training was performed.");
		}

		Instance instance = createTestInstance(p);

		double cls = 0;
		try {
			cls = model.classifyInstance(instance);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		String e = this.dataset.classAttribute().value((int) cls);
		Eligibility eligibility = Eligibility.get(e);

		LOG.debug("Criterion {} was {} for patient {} {} {}.", this.criterion.name(), eligibility, p.getID(), cls,
				(int) cls);
		return eligibility;
	}
}
