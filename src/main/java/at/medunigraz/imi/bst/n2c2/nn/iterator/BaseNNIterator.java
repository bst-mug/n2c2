package at.medunigraz.imi.bst.n2c2.nn.iterator;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.nn.input.InputRepresentation;
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

    protected final InputRepresentation inputRepresentation;

    protected int truncateLength;

    protected List<Patient> patients;   // TODO separate train and test data, allow iterator on test
    protected int cursor = 0;

    protected int batchSize;

    /**
     * Constructor used for first training, initializes `truncateLength` to the longest sequence in training data.
     *
     * @param patients
     * @param inputRepresentation
     * @param batchSize
     */
    public BaseNNIterator(List<Patient> patients, InputRepresentation inputRepresentation, int batchSize) {
        this.inputRepresentation = inputRepresentation;
        this.patients = patients;
        this.batchSize = batchSize;
        this.truncateLength = getLongestSequenceSize(patients);
    }

    /**
     * Constructor used for first training, uses a fixed `truncateLength` that can be shorter than the longest sequence in
     * training data.
     *
     * @param patients
     * @param inputRepresentation
     * @param truncateLength
     * @param batchSize
     */
    public BaseNNIterator(List<Patient> patients, InputRepresentation inputRepresentation, int truncateLength, int batchSize) {
        this.inputRepresentation = inputRepresentation;
        this.patients = patients;
        this.batchSize = batchSize;
        this.truncateLength = truncateLength;
    }

    /**
     * Constructor used for loading models from disk, uses a fixed `truncateLength`.
     *
     * @param inputRepresentation
     * @param truncateLength
     * @param batchSize
     */
    public BaseNNIterator(InputRepresentation inputRepresentation, int truncateLength, int batchSize) {
        this.inputRepresentation = inputRepresentation;
        this.batchSize = batchSize;
        this.truncateLength = truncateLength;
    }

    /**
     * Fill multi-hot vector for mulit label classification.
     */
    protected List<Boolean> patientToBinaryMultiHotVector(Patient patient) {
        List<Boolean> binaryMultiHotVector = new ArrayList<>();
        for (Criterion criterion : Criterion.classifiableValues()) {
            binaryMultiHotVector.add(patient.getEligibility(criterion).equals(Eligibility.MET));
        }
        return binaryMultiHotVector;
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
        // Empty
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

    /**
     * Get next training batch.
     *
     * @param num Minibatch size.
     * @return DatSet
     */
    public DataSet getNext(int num) {
        HashMap<Integer, List<Boolean>> binaryMultiHotVectorMap = new HashMap<>();

        // load patient batch
        int maxLength = 0;
        List<List<String>> patientUnits = new ArrayList<>(num);
        for (int i = 0; i < num && cursor < totalExamples(); i++) {
            List<String> units = getUnits(patients.get(cursor).getText());
            patientUnits.add(units);

            maxLength = Math.max(maxLength, units.size());

            binaryMultiHotVectorMap.put(i, patientToBinaryMultiHotVector(patients.get(cursor)));
            cursor++;
        }

        // truncate if sequence is longer than maxSentences
        if (maxLength > getTruncateLength())
            maxLength = getTruncateLength();

        INDArray features = Nd4j.create(new int[]{patientUnits.size(), inputRepresentation.getVectorSize(), maxLength}, 'f');
        INDArray labels = Nd4j.create(new int[]{patientUnits.size(), totalOutcomes(), maxLength}, 'f');

        INDArray featuresMask = Nd4j.zeros(patientUnits.size(), maxLength);
        INDArray labelsMask = Nd4j.zeros(patientUnits.size(), maxLength);

        int[] temp = new int[2];
        for (int i = 0; i < patientUnits.size(); i++) {
            List<String> units = patientUnits.get(i);
            temp[0] = i;

            // get word vectors for each token in narrative
            for (int j = 0; j < units.size() && j < maxLength; j++) {
                String sentence = units.get(j);

                // get vector presentation of sentence
                INDArray vector = inputRepresentation.getVector(sentence);
                features.put(new INDArrayIndex[]{NDArrayIndex.point(i), NDArrayIndex.all(), NDArrayIndex.point(j)},
                    vector);

                temp[1] = j;
                featuresMask.putScalar(temp, 1.0);
            }

            int lastIdx = Math.min(units.size(), maxLength);

            // set binary multi-labels
            List<Boolean> binaryMultiHotVector = binaryMultiHotVectorMap.get(i);
            int labelIndex = 0;
            for (Boolean label : binaryMultiHotVector) {
                labels.putScalar(new int[]{i, labelIndex, lastIdx - 1}, label ? 1.0 : 0.0);
                labelIndex++;
            }

            // out exists at the final step of the sequence
            labelsMask.putScalar(new int[]{i, lastIdx - 1}, 1.0);
        }
        return new DataSet(features, labels, featuresMask, labelsMask);
    }

    private int getLongestSequenceSize(List<Patient> patients) {
        int maxUnits = 0;
        for (Patient patient : patients) {
            List<String> units = getUnits(patient.getText());
            maxUnits = Math.max(maxUnits, units.size());
        }
        return maxUnits;
    }

    /**
     * Load features from narrative.
     *
     * @param reviewContents Narrative content.
     * @param maxLength      Maximum length of token series length.
     * @return Time series feature presentation of narrative.
     */
    public INDArray loadFeaturesForNarrative(String reviewContents, int maxLength) {
        List<String> units = getUnits(reviewContents);

        int outputLength = Math.min(maxLength, units.size());
        INDArray features = Nd4j.create(1, inputRepresentation.getVectorSize(), outputLength);

        for (int j = 0; j < units.size() && j < outputLength; j++) {
            String unit = units.get(j);
            INDArray vector = inputRepresentation.getVector(unit);
            features.put(new INDArrayIndex[]{NDArrayIndex.point(0), NDArrayIndex.all(), NDArrayIndex.point(j)},
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

    @Override
    public String toString() {
        return getClass().getSimpleName() +
            "{truncateLength=" + truncateLength +
            ",batchSize=" + batchSize +
            "}";
    }
}
