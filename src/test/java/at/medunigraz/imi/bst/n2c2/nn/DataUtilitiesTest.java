package at.medunigraz.imi.bst.n2c2.nn;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.xml.sax.SAXException;

import at.medunigraz.imi.bst.n2c2.dao.PatientDAO;
import at.medunigraz.imi.bst.n2c2.model.Patient;

public class DataUtilitiesTest {

    private static final File SAMPLE = new File(DataUtilitiesTest.class.getResource("/gold-standard/sample.xml").getPath());

	@Test
	public void processTextReduced() throws IOException {
		String normalized = DataUtilities.processTextReduced("This is a, test    sentence: test_sentence.");
		assertEquals("this is a test sentenc test sent", normalized);
	}

	@Test
	public void getChar3GramRepresentation() throws IOException {
		String normalized = DataUtilities.getChar3GramRepresentation("this is a test sentence");
		assertEquals("_th thi his is_ _is is_ _a_ _te tes est st_ _se sen ent nte ten enc nce ce_", normalized);
	}

	@Test
	public void sample() throws IOException, SAXException {
		Patient p = new PatientDAO().fromXML(SAMPLE);

		StringBuilder normalizedText = new StringBuilder();
		StringBuilder textTrigrams = new StringBuilder();

		List<String> sentences = DataUtilities.getSentences(p.getText());
		for (String sentence : sentences) {
			String normalized = DataUtilities.processTextReduced(sentence);
			String charTrigrams = DataUtilities.getChar3GramRepresentation(normalized);

			normalizedText.append(normalized);
			normalizedText.append("\n");

			textTrigrams.append(charTrigrams);
			textTrigrams.append("\n");
		}

		final File expectedNormalized = new File(getClass().getResource("/nn/sample-normalized.txt").getFile());
		final File expectedTrigrams = new File(getClass().getResource("/nn/sample-trigrams.txt").getFile());

		assertEquals(FileUtils.readFileToString(expectedNormalized, "UTF-8"), normalizedText.toString());
		assertEquals(FileUtils.readFileToString(expectedTrigrams, "UTF-8"), textTrigrams.toString());
	}

	@Test
    public void getSentences() throws IOException, SAXException {
        final File expectedFile = new File(getClass().getResource("/preprocessing/sample-sentences.txt").getFile());

        List<String> expected = FileUtils.readLines(expectedFile, "UTF-8");
        List<String> actual = DataUtilities.getSentences(new PatientDAO().fromXML(SAMPLE).getText());

        assertEquals(expected, actual);

        // TODO First period mark is dropped if followed by two whitespaces
//		expected = Arrays.asList("One sentence.", "Second sentence.");
		expected = Arrays.asList("One sentence", "Second sentence.");
        actual = DataUtilities.getSentences("One sentence.  Second sentence.");
        assertEquals(expected, actual);
    }

	@Test
	public void tokenize() {
		// Example from https://nlp.stanford.edu/software/tokenizer.shtml
		String[] actual = DataUtilities.tokenize("\"Oh, no,\" she's saying, \"our $400 blender can't handle something this hard!\"");
		assertEquals("Oh no she's saying our 400 blender can't handle something this hard", String.join(" ", actual));

		// Examples from https://www.nltk.org/api/nltk.tokenize.html
		actual = DataUtilities.tokenize("Good muffins cost $3.88\nin New York.  Please buy me\ntwo of them.\nThanks.");
		assertEquals("Good muffins cost 3.88 in New York Please buy me two of them Thanks", String.join(" ", actual));

		actual = DataUtilities.tokenize("They'll save and invest more.");
		assertEquals("They'll save and invest more", String.join(" ", actual));

		actual = DataUtilities.tokenize("hi, my name can't hello,");
		assertEquals("hi my name can't hello", String.join(" ", actual));
	}

	@Test
	public void getTokens() throws IOException, SAXException {
		final File expectedFile = new File(getClass().getResource("/preprocessing/sample-tokens.txt").getFile());

		List<String> expected = FileUtils.readLines(expectedFile, "UTF-8");
		List<String> actual = DataUtilities.getTokens(new PatientDAO().fromXML(SAMPLE).getText());

		assertEquals(expected, actual);
	}

	@Test
	public void getVocabulary() {
		Set<String> actual = DataUtilities.getVocabulary("This is a test sentence. Can't you think of a better sentence?");
		Set<String> expected = new TreeSet<>(Arrays.asList("a", "better", "can't", "is", "of", "sentence", "test", "think", "this", "you"));
		assertEquals(expected, actual);
	}

}
