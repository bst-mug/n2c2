package at.medunigraz.imi.bst.n2c2.nn;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.AttributeFactory;

/**
 * Refactored from dl4j examples.
 *
 * @author Markus
 *
 */
public abstract class DataUtilities {

    private static final Pattern WHITESPACES = Pattern.compile("\\p{javaWhitespace}+");

    private static String[] tokenStreamToArray(TokenStream stream) throws IOException {
        CharTermAttribute charTermAttribute = stream.addAttribute(CharTermAttribute.class);
        stream.reset();

        ArrayList<String> ret = new ArrayList<>();
        while (stream.incrementToken()) {
            ret.add(charTermAttribute.toString());
        }

        stream.end();
        stream.close();

        return ret.toArray(new String[0]);
    }

    private static TokenStream getTokenStream(Reader reader) {
        AttributeFactory factory = AttributeFactory.DEFAULT_ATTRIBUTE_FACTORY;

        StandardTokenizer standardTokenizer = new StandardTokenizer(factory);
        standardTokenizer.setReader(reader);

        return standardTokenizer;
    }

    /**
     *
     *
     * @param reader
     * @return
     */
    public static TokenStream getQuickViewStreamReduced(Reader reader) {
        TokenStream result = getTokenStream(reader);

        result = new LowerCaseFilter(result);

        // negations tokens are included therefore no use
        // result = new StopFilter(result, stopWords);
        result = new SnowballFilter(result, "English");

        return result;
    }

    /**
     *
     *
     * @param textToProcess
     * @return
     */
    public static String processTextReduced(String textToProcess) {
        TokenStream stream = getQuickViewStreamReduced(new StringReader(textToProcess));
        String[] tokens = new String[0];
        try {
            tokens = tokenStreamToArray(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String normalized = String.join(" ", tokens);;

        // post normalization
        normalized = normalized.replaceAll("[\\.\\,\\_\\:]+", " ");
        normalized = normalized.replaceAll("[\\s]+", " ");
        normalized = normalized.trim();

        return normalized;
    }

    public static String getChar3GramRepresentation(String toProcess) throws IOException {

        String charNGramRepresentation = "";
        String embedding = "_";

        NGramTokenizer nGramTokenizer = new NGramTokenizer(3, 3);
        CharTermAttribute charTermAttribute = nGramTokenizer.addAttribute(CharTermAttribute.class);
        for (String split : toProcess.split("\\s")) {

            nGramTokenizer.setReader(new StringReader(embedding + split + embedding));
            nGramTokenizer.reset();

            while (nGramTokenizer.incrementToken()) {
                String characterNGram = charTermAttribute.toString();
                charNGramRepresentation += characterNGram + " ";
            }
            nGramTokenizer.end();
            nGramTokenizer.close();
        }
        return charNGramRepresentation.trim();
    }

    /**
     * Cleans the text and detect sentences using a rule-based algorithm.
     *
     * @param narrative
     * @return
     */
    public static List<String> getSentences(String narrative) {
        String cleanedNarrative = clean(narrative);

        String abbreviations = "\\d|[mM][rR]|[dD][rR]|[dD][rR][sS]|[sM][sS]|[cC]";

        // new line split logic
        // Positive lookahead ensure next character is alphanumeric, thus removing duplicate linebreaks.
        String[] newLineSplits = cleanedNarrative.split("\n(?=[A-Z]|[0-9])");
        ArrayList<String> sentences = new ArrayList<String>();

        // period character split logic
        for (String newLineSplit : newLineSplits) {
            // Remove duplicate linebreaks eventually found in the split (see comment above).
            newLineSplit = newLineSplit.replaceAll("[\r\n\\s]+", " ").trim();
            if (newLineSplit.length() > 0) {
                // Split into period markers (not preceeded by abbreviations) followed by any number of whitespaces.
                sentences.addAll(Arrays.asList(newLineSplit.split("(?<!" + abbreviations + ")(\\.)(\\s+)")));
            }
        }

        return sentences;
    }

    /**
     * Cleans a text by removing symbols, special characters, and deduplicated spaces and period marks.
     *
     * @param text
     * @return
     */
    public static String clean(String text) {
        String cleanPatternA = "[\t\\*_\\%=#]+";
        String cleanPatternB = "&nbsp;|<BR>|\\s+|--";

        String cleanedNarrative = "";
        String tempString = "";

        // cleansing beginning input lines
        try {
            List<String> lines = IOUtils.readLines(new StringReader(text));
            for (String line : lines) {
                if (line.length() > 0) {
                    tempString = line.replaceAll(cleanPatternA, " ");
                    tempString = tempString.replaceAll(cleanPatternB, " ");
                    tempString = tempString.replaceAll("\\.+", ".").trim();
                    if (tempString.length() > 0)
                        cleanedNarrative += tempString + "\n";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cleanedNarrative;
    }

    public static String removeWhitespaces(String text) {
        return WHITESPACES.matcher(text).replaceAll(" ");
    }

    /**
     * Tokenizes a given sentence. Use `getTokens` if you're tokenizing a full text.
     *
     * @param text
     * @return
     */
    public static String[] tokenize(String text) {
        TokenStream stream = getTokenStream(new StringReader(text));
        try {
            return tokenStreamToArray(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cleans the text, does sentence detection, tokenization and lowercasing.
     * Returns a list of tokens.
     *
     * @param text
     * @return
     */
    public static List<String> getTokens(String text) {
        List<String> ret = new ArrayList<>();

        List<String> sentences = getSentences(text);
        for (String sentence : sentences) {
            String[] tokens = tokenize(sentence);
            for (String token : tokens) {
                ret.add(token.toLowerCase());
            }
        }

        return ret;
    }

    /**
     * Cleans the text, does sentence detection, tokenization and lowercasing.
     * Returns a list of sentences, with each token separated by space.
     *
     * @param text
     * @return
     */
    public static List<String> getTokenizedSentences(String text) {
        List<String> ret = new ArrayList<>();

        List<String> sentences = getSentences(text);
        for (String sentence : sentences) {
            String[] tokens = tokenize(sentence);
            ret.add(String.join(" ", tokens).toLowerCase());
        }

        return ret;
    }

    /**
     * Extracts the unique set of words used in a text.
     *
     * @param text
     * @return
     */
    public static Set<String> getVocabulary(String text) {
        return new TreeSet<>(getTokens(text));
    }
}
