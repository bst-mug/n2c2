package at.medunigraz.imi.bst.n2c2.nn;

import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

/**
 * Helper class to dump the unique set of tokenized words used in the n2c2 collection.
 *
 * This is used e.g. to manually query BioSentVec vectors using fasttext original tool.
 */
public class VocabularyDumper {

    public static void main(String[] args) throws IOException {
        final File trainFolder = new File("data/train");
        final File testFolder = new File("data/test");

        final File vocabularyFile = new File("vocab.txt");

        List<Patient> trainPatients = DatasetUtil.loadFromFolder(trainFolder);
        List<Patient> testPatients = DatasetUtil.loadFromFolder(testFolder);

        TreeSet<String> vocabulary = new TreeSet<>();

        trainPatients.forEach(p -> vocabulary.addAll(DataUtilities.getVocabulary(p.getText())));
        testPatients.forEach(p -> vocabulary.addAll(DataUtilities.getVocabulary(p.getText())));

        FileUtils.writeLines(vocabularyFile, vocabulary);
    }
}
