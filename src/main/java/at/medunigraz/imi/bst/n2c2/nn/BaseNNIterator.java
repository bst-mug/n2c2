package at.medunigraz.imi.bst.n2c2.nn;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class BaseNNIterator implements DataSetIterator {


    protected List<Patient> patients;
    protected int cursor = 0;

    protected int batchSize;
    public int vectorSize;

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
        return vectorSize;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.nd4j.linalg.dataset.api.iterator.DataSetIterator#totalOutcomes()
     */
    @Override
    public int totalOutcomes() {
        return 13;
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

    public abstract DataSet getNext(int num);
}
