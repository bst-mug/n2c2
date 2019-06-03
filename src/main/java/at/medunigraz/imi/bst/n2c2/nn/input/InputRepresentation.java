package at.medunigraz.imi.bst.n2c2.nn.input;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.*;

public interface InputRepresentation {

    INDArray getVector(String unit);

    boolean hasRepresentation(String unit);

    int getVectorSize();

    void save(File model);

    void load(File model);
}
