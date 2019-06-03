package at.medunigraz.imi.bst.n2c2.nn.architecture;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public interface Architecture {

    MultiLayerNetwork getNetwork(int nIn);
}
