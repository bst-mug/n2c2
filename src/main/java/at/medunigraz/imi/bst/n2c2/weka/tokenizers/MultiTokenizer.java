package at.medunigraz.imi.bst.n2c2.weka.tokenizers;

import weka.core.tokenizers.Tokenizer;

import java.util.ArrayList;

public class MultiTokenizer extends Tokenizer {

    private ArrayList<Tokenizer> tokenizers = new ArrayList<>();

    public void addTokenizer(Tokenizer tokenizer) {
        tokenizers.add(tokenizer);
    }

    /**
     * Returns a string describing the stemmer
     *
     * @return a description suitable for displaying in the explorer/experimenter
     * gui
     */
    @Override
    public String globalInfo() {
        return getClass().getName();
    }

    /**
     * Tests if this enumeration contains more elements.
     *
     * @return true if and only if this enumeration object contains at least one
     * more element to provide; false otherwise.
     */
    @Override
    public boolean hasMoreElements() {
        for (Tokenizer tokenizer : tokenizers) {
            if (tokenizer.hasMoreElements()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the next element of this enumeration if this enumeration object has
     * at least one more element to provide.
     *
     * @return the next element of this enumeration.
     */
    @Override
    public String nextElement() {
        for (Tokenizer tokenizer : tokenizers) {
            if (tokenizer.hasMoreElements()) {
                return tokenizer.nextElement();
            }
        }

        return "";
    }

    /**
     * Sets the string to tokenize. Tokenization happens immediately.
     *
     * @param s the string to tokenize
     */
    @Override
    public void tokenize(String s) {
        for (Tokenizer tokenizer : tokenizers) {
            tokenizer.tokenize(s);
        }
    }

    /**
     * Returns the revision string.
     *
     * @return the revision
     */
    @Override
    public String getRevision() {
        return "1";
    }
}
