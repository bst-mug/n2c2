package at.medunigraz.imi.bst.n2c2.nn.iterator;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.nn.input.InputRepresentation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import java.io.File;
import java.util.*;

public abstract class BaseNNIterator implements DataSetIterator {
    private static final Logger LOG = LogManager.getLogger();

    protected final InputRepresentation inputRepresentation;

    protected int truncateLength;

    protected List<Patient> patients;   // TODO separate train and test data, allow iterator on test
    protected int cursor = 0;

    protected int batchSize;

    public BaseNNIterator(InputRepresentation inputRepresentation) {
        this.inputRepresentation = inputRepresentation;
    }

    /**
     * Fill multi-hot vector for mulit label classification.
     *
     * @param binaryMultiHotVector
     */
    protected void fillBinaryMultiHotVector(List<Boolean> binaryMultiHotVector) {
        for (Criterion criterion : Criterion.classifiableValues()) {
            binaryMultiHotVector.add(patients.get(cursor).getEligibility(criterion).equals(Eligibility.MET));
        }
    }

    public InputRepresentation getInputRepresentation() {
        return inputRepresentation;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#totalExamples()
     */
    @Override
    public int totalExamples() {
        return this.patients.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#inputColumns()
     */
    @Override
    public int inputColumns() {
        return inputRepresentation.getVectorSize();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#totalOutcomes()
     */
    @Override
    public int totalOutcomes() {
        return Criterion.classifiableValues().length;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#reset()
     */
    @Override
    public void reset() {
        cursor = 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.nd4j.linalg.dataset.api.iterator.DataSetIterator#resetSupported()
     */
    public boolean resetSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.nd4j.linalg.dataset.api.iterator.DataSetIterator#asyncSupported()
     */
    @Override
    public boolean asyncSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#batch()
     */
    @Override
    public int batch() {
        return batchSize;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#cursor()
     */
    @Override
    public int cursor() {
        return cursor;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#numExamples()
     */
    @Override
    public int numExamples() {
        return totalExamples();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.nd4j.linalg.dataset.api.iterator.DataSetIterator#setPreProcessor(org.
     * nd4j.linalg.dataset.api.DataSetPreProcessor)
     */
    @Override
    public void setPreProcessor(DataSetPreProcessor preProcessor) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#getLabels()
     */
    @Override
    public List<String> getLabels() {
        return Arrays.asList("ABDOMINAL", "ADVANCED-CAD", "ALCOHOL-ABUSE", "ASP-FOR-MI", "CREATININE", "DIETSUPP-2MOS",
                "DRUG-ABUSE", "ENGLISH", "HBA1C", "KETO-1YR", "MAJOR-DIABETES", "MAKES-DECISIONS", "MI-6MOS");
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
        return cursor < numExamples();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.nd4j.linalg.dataset.api.iterator.DataSetIterator#getPreProcessor()
     */
    @Override
    public DataSetPreProcessor getPreProcessor() {
        throw new UnsupportedOperationException("Not implemented");
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Iterator#next()
     */
    @Override
    public DataSet next() {
        return next(batchSize);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#next(int)
     */
    @Override
    public DataSet next(int num) {
        if (cursor >= patients.size())
            throw new NoSuchElementException();

        return getNext(num);
    }

    public int getTruncateLength() {
        return truncateLength;
    }

    public abstract DataSet getNext(int num);

    /**
     * Load features from narrative.
     *
     * @param reviewContents
     *            Narrative content.
     * @param maxLength
     *            Maximum length of token series length.
     * @return Time series feature presentation of narrative.
     */
    public INDArray loadFeaturesForNarrative(String reviewContents, int maxLength) {
        List<String> units = getUnits(reviewContents);

        int outputLength = Math.min(maxLength, units.size());
        INDArray features = Nd4j.create(1, inputRepresentation.getVectorSize(), outputLength);

        for (int j = 0; j < units.size() && j < outputLength; j++) {
            String unit = units.get(j);
            INDArray vector = inputRepresentation.getVector(unit);
            features.put(new INDArrayIndex[] { NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(j) },
                    vector);
        }
        return features;
    }

    protected abstract List<String> getUnits(String text);

    public void save(File model) {
        inputRepresentation.save(model);
    }

    public void load(File model) {
        inputRepresentation.load(model);
    }
}
