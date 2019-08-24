package at.medunigraz.imi.bst.n2c2.nn.architecture;

import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class LSTMArchitecture implements Architecture {

    private static final int LSTM_LAYER_SIZE = 64;
    private static final double L2_REGULARIZATION = 1e-5;
    private static final double LEARNING_RATE = 2e-2;

    @Override
    public MultiLayerNetwork getNetwork(int nIn) {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(0)
                .updater(Adam.builder().learningRate(LEARNING_RATE).build()).regularization(true).l2(L2_REGULARIZATION)
                .dropOut(0.5).weightInit(WeightInit.XAVIER)
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                .gradientNormalizationThreshold(1.0).trainingWorkspaceMode(WorkspaceMode.SEPARATE)
                .inferenceWorkspaceMode(WorkspaceMode.SEPARATE) // https://deeplearning4j.org/workspaces
                .list().layer(0, new GravesLSTM.Builder().nIn(nIn).nOut(LSTM_LAYER_SIZE).activation(Activation.TANH).build())
                .layer(1,
                        new RnnOutputLayer.Builder().activation(Activation.SIGMOID)
                                .lossFunction(LossFunctions.LossFunction.XENT).nIn(LSTM_LAYER_SIZE).nOut(13).build())
                .pretrain(false).backprop(true).build();

        // for truncated backpropagation over time
        // .backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength)
        // .tBPTTBackwardLength(tbpttLength).pretrain(false).backprop(true).build();

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));
        return net;
    }

    @Override
    public String toString() {
        return "LSTMArchitecture{"
                + "layerSize=" + LSTM_LAYER_SIZE
                + ",regularization=" + L2_REGULARIZATION
                + ",learningRate=" + LEARNING_RATE
                + '}';
    }
}
