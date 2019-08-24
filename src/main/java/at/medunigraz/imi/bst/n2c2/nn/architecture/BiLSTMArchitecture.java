package at.medunigraz.imi.bst.n2c2.nn.architecture;

import at.medunigraz.imi.bst.n2c2.model.Criterion;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.GravesBidirectionalLSTM;
import org.deeplearning4j.nn.conf.layers.GravesLSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.conf.preprocessor.FeedForwardToRnnPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.RnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.AdaGrad;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * SIGMOID activation and XENT loss function for binary multi-label classification.
 */
public class BiLSTMArchitecture implements Architecture {

    private static final int FF_LAYER_SIZE = 150;
    private static final int LSTM_LAYER_SIZE = 128;
    private static final double L2_REGULARIZATION = 0.01;
    private static final double CORE_LEARNING_RATE = 0.04;
    private static final double DENSE_LEARNING_RATE = 0.01;
    private static final double GRAVES_LEARNING_RATE = 0.008;

    @Override
    public MultiLayerNetwork getNetwork(int nIn) {
        // seed for reproducibility
        final int seed = 12345;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed)
            .updater(AdaGrad.builder().learningRate(CORE_LEARNING_RATE).build()).regularization(true).l2(L2_REGULARIZATION)
            .weightInit(WeightInit.XAVIER).gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
            .gradientNormalizationThreshold(1.0).trainingWorkspaceMode(WorkspaceMode.SINGLE)
            .inferenceWorkspaceMode(WorkspaceMode.SINGLE).list()

            .layer(0, new DenseLayer.Builder().activation(Activation.RELU).nIn(nIn).nOut(FF_LAYER_SIZE)
                .weightInit(WeightInit.RELU).updater(AdaGrad.builder().learningRate(DENSE_LEARNING_RATE).build())
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                .gradientNormalizationThreshold(10).build())

            .layer(1, new DenseLayer.Builder().activation(Activation.RELU).nIn(FF_LAYER_SIZE).nOut(FF_LAYER_SIZE)
                .weightInit(WeightInit.RELU).updater(AdaGrad.builder().learningRate(DENSE_LEARNING_RATE).build())
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                .gradientNormalizationThreshold(10).build())

            .layer(2, new DenseLayer.Builder().activation(Activation.RELU).nIn(FF_LAYER_SIZE).nOut(FF_LAYER_SIZE)
                .weightInit(WeightInit.RELU).updater(AdaGrad.builder().learningRate(DENSE_LEARNING_RATE).build())
                .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                .gradientNormalizationThreshold(10).build())

            .layer(3,
                new GravesBidirectionalLSTM.Builder().nIn(FF_LAYER_SIZE).nOut(LSTM_LAYER_SIZE)
                    .updater(AdaGrad.builder().learningRate(GRAVES_LEARNING_RATE).build())
                    .activation(Activation.SOFTSIGN).build())

            .layer(4,
                new GravesLSTM.Builder().nIn(LSTM_LAYER_SIZE).nOut(LSTM_LAYER_SIZE)
                    .updater(AdaGrad.builder().learningRate(GRAVES_LEARNING_RATE).build())
                    .activation(Activation.SOFTSIGN).build())

            .layer(5, new RnnOutputLayer.Builder().activation(Activation.SIGMOID)
                .lossFunction(LossFunctions.LossFunction.XENT).nIn(LSTM_LAYER_SIZE).nOut(Criterion.classifiableValues().length).build())

            .inputPreProcessor(0, new RnnToFeedForwardPreProcessor())
            .inputPreProcessor(3, new FeedForwardToRnnPreProcessor()).pretrain(false).backprop(true).build();

        // .backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength).tBPTTBackwardLength(tbpttLength)

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));
        return net;
    }

    @Override
    public String toString() {
        return "BiLSTMArchitecture{"
            + "layerSize=" + LSTM_LAYER_SIZE
            + ",regularization=" + L2_REGULARIZATION
            + ",learningRate=" + CORE_LEARNING_RATE
            + '}';
    }
}
