package at.medunigraz.imi.bst.n2c2.classifier;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FakeClassifier extends CriterionBasedClassifier {

    private static final File TRAIN_FOLDER = new File("data/train");
    private static final File TEST_FOLDER = new File("data/test");

    private static final List<Patient> PATIENTS = Stream.concat(DatasetUtil.loadFromFolder(TRAIN_FOLDER).stream(),
            DatasetUtil.loadFromFolder(TEST_FOLDER).stream()).collect(Collectors.toList());

    public FakeClassifier(Criterion c) {
        super(c);
    }

    @Override
    public void train(List<Patient> examples) {
        return;
    }

    @Override
    public Eligibility predict(Patient p) {
        Patient cheat = DatasetUtil.findById(p.getID(), PATIENTS);
        if (cheat == null) {
            throw new RuntimeException(String.format("Could not find patient %s for cheating!", p.getID()));
        }
        return cheat.getEligibility(criterion);
    }
}
