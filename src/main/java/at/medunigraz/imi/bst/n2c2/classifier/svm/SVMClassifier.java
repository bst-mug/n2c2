package at.medunigraz.imi.bst.n2c2.classifier.svm;

import at.medunigraz.imi.bst.n2c2.classifier.CriterionBasedClassifier;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.*;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stemmers.Stemmer;
import weka.core.tokenizers.AlphabeticTokenizer;
import weka.core.tokenizers.Tokenizer;
import weka.filters.Filter;
import weka.filters.MultiFilter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SVMClassifier extends CriterionBasedClassifier {

    private static final Logger LOG = LogManager.getLogger();

    /**
     * ID, Text and Eligibility
     */
    private static final int NUM_ATTRIBUTES = 3;
    private static final int ID_INDEX = 0;
    private static final int TEXT_INDEX = 1;
    private static final int ELIGIBILITY_INDEX = 2;

    private Classifier model;
    private Instances dataset;

    public SVMClassifier(Criterion criterion) {
        super(criterion);
        reset();
    }

    public void reset() {
        // TODO check what should be reset every time and what not
        model = initializeModel();
        dataset = null;
    }

    private Classifier initializeModel() {
        // TODO non-deterministic. see https://weka.wikispaces.com/LibSVM
        LibSVM svm = new LibSVM();

        // -K <int>
        // Set type of kernel function (default: 2)
        // 0 = linear: u'*v
        // 1 = polynomial: (gamma*u'*v + coef0)^degree
        // 2 = radial basis function: exp(-gamma*|u-v|^2)
        // 3 = sigmoid: tanh(gamma*u'*v + coef0)
        // Linear kernel is commonly recommended for text classification
        svm.setKernelType(new SelectedTag(0, LibSVM.TAGS_KERNELTYPE));

        // FIXME optimize
        svm.setCost(1);

        // ID index is removed from classification, but is available for debugging
        Remove remove = new Remove();
        remove.setAttributeIndicesArray(new int[]{ID_INDEX});
        //remove.setAttributeIndices("1,2,4,5");

        MultiFilter mf = new MultiFilter();
        Filter f = initializeFilter();
        mf.setFilters(new Filter[]{remove, f});

        Classifier model = new FilteredClassifier();

        ((FilteredClassifier) model).setFilter(mf);
        ((FilteredClassifier) model).setClassifier(svm);

        return model;
    }

    private Filter initializeFilter() {
        StringToWordVector f = new StringToWordVector();
        f.setAttributeIndices("first");
        f.setDoNotOperateOnPerClassBasis(true);
        f.setLowerCaseTokens(true);
        f.setMinTermFreq(1);
        f.setOutputWordCounts(true);

        // TODO evaluate impact
        //f.setStemmer(getStemmer());

        f.setTokenizer(getTokenizer());

        // TODO check whether stopwords are removed before stemming
        //f.setStopwords(new File(refDir + "stopwords.txt"));
        //f.setUseStoplist(Constants.CONFIG.getStoplist());

        // Enable only for performance reasons
        //f.setWordsToKeep(5000);


        // TODO L2 norm

        f.setTFTransform(true);
        f.setIDFTransform(true);
        return f;
    }

    private List<Attribute> initializeAttributes() {
        // TODO method could be static
        List<Attribute> attributes = new ArrayList<>();

        List<String> eligibilityValues = new ArrayList<>();
        Arrays.stream(Eligibility.values()).forEach(e -> eligibilityValues.add(e.toString()));

        List<String> textValue = null;
        attributes.add(new Attribute("id", textValue));
        attributes.add(new Attribute("text", textValue));
        attributes.add(new Attribute("eligibility", eligibilityValues));

        return attributes;
    }


    @Override
    public void train(List<Patient> examples) {
        reset();

        createDataset(examples);

        LOG.debug("Training SVM with {} patients...", examples.size());

        // TODO dataset file cache

        // TODO other kernels
        // TODO other multi-class strategies
        try {
            model.buildClassifier(dataset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Instances createEmptyDataset() {
        ArrayList<Attribute> attributes = (ArrayList<Attribute>) initializeAttributes();

        // TODO maybe remove dependency on attributes by using a different constructor?
        // TODO capacity > 1
        Instances data = new Instances(this.getClass().getName(), attributes, 1);

        data.setClassIndex(ELIGIBILITY_INDEX);

        return data;
    }

    private void createDataset(List<Patient> patients) {
        dataset = createEmptyDataset();

        // TODO might be used for prediction as well
        patients.forEach(p -> dataset.add(createTrainingInstance(p)));
    }

    private Instance createTrainingInstance(Patient p) {
        return createInstance(p, true);
    }

    private Instance createTestInstance(Patient p) {
        return createInstance(p, false);
    }

    private Instance createInstance(Patient p, boolean trainNotTest) {
        DenseInstance instance = new DenseInstance(NUM_ATTRIBUTES);

        // TODO receive as parameter...
        if (trainNotTest) {
            instance.setDataset(dataset);
        } else {
            // TODO check if it works...
            instance.setDataset(createEmptyDataset());
        }

        // ID might be null during testing
        if (p.getID() != null) {
            instance.setValue(ID_INDEX, p.getID());
        }

        instance.setValue(TEXT_INDEX, p.getText());

        // TODO move to caller
        // Answer should not be given when testing...
        if (trainNotTest) {
            instance.setValue(ELIGIBILITY_INDEX, p.getEligibility(criterion).toString());
        }

        return instance;
    }

    @Override
    public Eligibility predict(Patient p) {
        // TODO consider overwriting as well for a single test set
        // List<Patient> predict(List<Patient> patientList)

        Instance instance = createTestInstance(p);

        double cls = 0;
        try {
            cls = model.classifyInstance(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // TODO check if needed
        instance.setClassValue(cls);

        // TODO check reset() was called
        // TODO process Patient in the same way as training

        Eligibility eligibility = Eligibility.get(instance.classAttribute().value((int) cls));

        LOG.debug("Predicted {} for patient {}.", eligibility, p.getID());

        return eligibility;
    }

    private Tokenizer getTokenizer() {
        // XXX AlphabeticTokenizer does not support diacritics (see hasMoreElements method).
        // This shouldn't be an issue in English though.
        Tokenizer tokenizer = new AlphabeticTokenizer();
        return tokenizer;
    }

    private Stemmer getStemmer() {
        // TODO consider Porter
        return new SnowballStemmer();
    }
}
