package at.medunigraz.imi.bst.n2c2.nn.input;

import at.medunigraz.imi.bst.n2c2.nn.DataUtilities;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.*;
import java.util.*;

public class CharacterTrigram implements InputRepresentation {

    private ArrayList<String> characterNGram_3 = new ArrayList<String>();

    private Map<String, Integer> char3GramToIdxMap = new HashMap<String, Integer>();

    public CharacterTrigram() {
        // TODO same as load
    }

    public CharacterTrigram(Map<Integer, List<String>> integerListMap) {
        // generate char 3 grams
        try {
            fillCharNGramsMaps(integerListMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // generate index
        this.createIndizes();
    }

    public void save(File model) {
        File root = model;

        try {
            // writing our character n-grams
            FileOutputStream fos = new FileOutputStream(new File(root, "characterNGram_3"));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(characterNGram_3);
            oos.flush();
            oos.close();
            fos.close();

            // writing our character n-grams
            fos = new FileOutputStream(new File(root, "char3GramToIdxMap"));
            oos = new ObjectOutputStream(fos);
            oos.writeObject(char3GramToIdxMap);
            oos.flush();
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(File model) {
        try {
            // read char 3-grams and index
            FileInputStream fis = new FileInputStream(new File(model, "characterNGram_3"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            ArrayList<String> characterNGram_3 = (ArrayList<String>) ois.readObject();

            this.characterNGram_3 = characterNGram_3;

            // read char 3-grams index
            fis = new FileInputStream(new File(model, "char3GramToIdxMap"));
            ois = new ObjectInputStream(fis);
            Map<String, Integer> char3GramToIdxMap_0 = (HashMap<String, Integer>) ois.readObject();
            this.char3GramToIdxMap = char3GramToIdxMap_0;

            Nd4j.getRandom().setSeed(12345);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public INDArray getVector(String unit) {
        return getChar3GramVectorToSentence(unit);
    }

    @Override
    public boolean hasRepresentation(String unit) {
        return char3GramToIdxMap.containsKey(unit);
    }

    @Override
    public int getVectorSize() {
        return characterNGram_3.size();
    }

    /**
     * Creates index for character 3-grams.
     */
    private void createIndizes() {
        // store indexes
        for (int i = 0; i < characterNGram_3.size(); i++)
            char3GramToIdxMap.put(characterNGram_3.get(i), i);
    }

    /**
     * Fills character 3-gram dictionary.
     *
     * @throws IOException
     */
    private void fillCharNGramsMaps(Map<Integer, List<String>> integerListMap) throws IOException {
        // TODO operate on a single List<String> with all sentences
        for (Map.Entry<Integer, List<String>> entry : integerListMap.entrySet()) {
            for (String line : entry.getValue()) {
                String normalized = DataUtilities.processTextReduced(line);
                String char3Grams = DataUtilities.getChar3GramRepresentation(normalized);

                // process character n-grams
                String[] char3Splits = char3Grams.split("\\s+");

                for (String split : char3Splits) {
                    if (!characterNGram_3.contains(split)) {
                        characterNGram_3.add(split);
                    }
                }
            }

            // adding out of dictionary entries
            characterNGram_3.add("OOD");
        }
    }

    /**
     * Sentence will be transformed to a character 3-gram vector.
     *
     * @param sentence
     *            Sentence which gets vector representation.
     * @return
     */
    public INDArray getChar3GramVectorToSentence(String sentence) {

        INDArray featureVector = Nd4j.zeros(getVectorSize());
        try {
            String normalized = DataUtilities.processTextReduced(sentence);
            String char3Grams = DataUtilities.getChar3GramRepresentation(normalized);

            // process character n-grams
            String[] char3Splits = char3Grams.split("\\s+");

            for (String split : char3Splits) {
                if (char3GramToIdxMap.get(split) == null) {
                    int nGramIndex = char3GramToIdxMap.get("OOD");
                    featureVector.putScalar(new int[] { nGramIndex }, 1.0);
                } else {
                    int nGramIndex = char3GramToIdxMap.get(split);
                    featureVector.putScalar(new int[] { nGramIndex }, 1.0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return featureVector;
    }
}
