package at.medunigraz.imi.bst.n2c2.runner;

import at.medunigraz.imi.bst.n2c2.model.Patient;
import at.medunigraz.imi.bst.n2c2.nn.DataUtilities;
import at.medunigraz.imi.bst.n2c2.util.DatasetUtil;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to dump the sentences used in the n2c2 collection.
 *
 * This is used to manually train word embeddings using e.g. fasttext.
 */
public class SentenceDumper {

    public static void main(String[] args) throws IOException {
        final File trainFolder = new File("data/train");

        final File sentencesFile = new File("sentences.txt");

        List<Patient> trainPatients = DatasetUtil.loadFromFolder(trainFolder);

        List<String> sentences = new ArrayList<>();

        trainPatients.forEach(p -> sentences.addAll(DataUtilities.getTokenizedSentences(p.getText())));

        FileUtils.writeLines(sentencesFile, sentences);
    }
}
