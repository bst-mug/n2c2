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

    private static final int DEFAULT_COST = 1;
    private static final int DEFAULT_WORDS_TO_KEEP = 1000;

    private Classifier model;
    private Instances dataset;
    private double cost;

    public SVMClassifier(Criterion criterion, double cost) {
        super(criterion);
        this.cost = cost;
        this.model = initializeModel();
        reset();
    }

    public SVMClassifier(Criterion criterion) {
        this(criterion, DEFAULT_COST);
    }

    public void reset() {
        this.dataset = createEmptyDataset();
    }

    private Classifier initializeModel() {
        // TODO non-deterministic. see https://weka.wikispaces.com/LibSVM
        LibSVM svm = new LibSVM();

        // TODO try other kernels
        // Set type of kernel function (default: 2)
        // 0 = linear: u'*v
        // 1 = polynomial: (gamma*u'*v + coef0)^degree
        // 2 = radial basis function: exp(-gamma*|u-v|^2)
        // 3 = sigmoid: tanh(gamma*u'*v + coef0)
        // Linear kernel is commonly recommended for text classification
        svm.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE));

        svm.setCost(cost);

        // ID index is removed from classification, but is available for debugging
        Remove remove = new Remove();
        remove.setAttributeIndicesArray(new int[]{ID_INDEX});

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

        // Overall, stemmer does not have any positive impact
        //f.setStemmer(getStemmer());

        f.setTokenizer(getTokenizer());

        // TODO check whether stopwords are removed before stemming
        //f.setStopwords(new File(refDir + "stopwords.txt"));
        //f.setUseStoplist(Constants.CONFIG.getStoplist());

        // Makes the default value explicit (even though it's optimal)
        f.setWordsToKeep(DEFAULT_WORDS_TO_KEEP);
        //f.setDictionaryFileToSaveTo(new File("dict.csv"));

        // Overall, normalization does not have any positive impact
        //f.setNormalizeDocLength(new SelectedTag(StringToWordVector.FILTER_NORMALIZE_ALL, StringToWordVector.TAGS_FILTER));

        f.setTFTransform(true);
        f.setIDFTransform(true);
        return f;
    }

    private List<Attribute> initializeAttributes() {
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

        // TODO might be used for prediction as well
        examples.forEach(p -> dataset.add(createTrainingInstance(p)));

        LOG.debug("Training SVM with {} patients...", examples.size());

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

    private Instance createTrainingInstance(Patient p) {
        Instance instance = createInstance(p, this.dataset);

        // Answer should be given only when training...
        instance.setValue(ELIGIBILITY_INDEX, p.getEligibility(criterion).toString());

        return instance;
    }

    private Instance createTestInstance(Patient p) {
        // TODO check if it works...
        Instances testDataset = createEmptyDataset();
        return createInstance(p, testDataset);
    }

    private Instance createInstance(Patient p, Instances dataset) {
        DenseInstance instance = new DenseInstance(NUM_ATTRIBUTES);
        instance.setDataset(dataset);

        // ID might be null during testing
        if (p.getID() != null) {
            instance.setValue(ID_INDEX, p.getID());
        }

        instance.setValue(TEXT_INDEX, p.getText());

        return instance;
    }

    @Override
    public Eligibility predict(Patient p) {
        // TODO consider overwriting as well for a single test set
        // List<Patient> predict(List<Patient> patientList)

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

        // TODO check if needed
        instance.setClassValue(cls);

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
        // XXX SnowballStemmer should be added to classpath.
        // Easiest way is to add https://mvnrepository.com/artifact/com.github.rholder/snowball-stemmer to pom.xml
        Stemmer stemmer = new SnowballStemmer();
        ((SnowballStemmer) stemmer).setStemmer("english");
        return stemmer;
    }
}
