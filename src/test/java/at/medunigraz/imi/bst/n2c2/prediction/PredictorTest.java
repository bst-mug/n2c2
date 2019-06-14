package at.medunigraz.imi.bst.n2c2.prediction;

import at.medunigraz.imi.bst.n2c2.classifier.factory.ClassifierFactory;
import at.medunigraz.imi.bst.n2c2.classifier.factory.FactoryProvider;
import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Criterion;
import at.medunigraz.imi.bst.n2c2.model.Eligibility;
import at.medunigraz.imi.bst.n2c2.model.Patient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class PredictorTest {

    private static final String TEST = "/test/";

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    private final File testFolder = new File(getClass().getResource(TEST).getFile());

    @Test
    public void loadTrainPredictSave() throws IOException, SAXException {
        final File predictionFolder = tempFolder.newFolder();
        final File trainingFolder = new File("data/train");

        final ClassifierFactory factory = FactoryProvider.getRBCFactory();

        new Predictor(factory).loadTrainPredictSave(trainingFolder, testFolder, predictionFolder);

        Patient p = new PatientDAO().fromXML(new File(predictionFolder, "test.xml"));

        for (Criterion criterion : Criterion.classifiableValues()) {
            System.out.println(criterion);

            if (criterion == Criterion.ENGLISH || criterion == Criterion.KETO_1YR || criterion == Criterion.MAKES_DECISIONS) {
                assertEquals(Eligibility.NOT_MET, p.getEligibility(criterion));
                continue;
            }

            assertEquals(Eligibility.MET, p.getEligibility(criterion));
        }
    }
}