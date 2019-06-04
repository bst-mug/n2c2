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

public class BiLSTMArchitecture implements Architecture {
    @Override
    public MultiLayerNetwork getNetwork(int nIn) {
        int nOutFF = 150;
        int lstmLayerSize = 128;
        double l2Regulization = 0.01;
        double adaGradCore = 0.04;
        double adaGradDense = 0.01;
        double adaGradGraves = 0.008;

        // seed for reproducibility
        final int seed = 12345;
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(seed)
                .updater(AdaGrad.builder().learningRate(adaGradCore).build()).regularization(true).l2(l2Regulization)
                .weightInit(WeightInit.XAVIER).gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                .gradientNormalizationThreshold(1.0).trainingWorkspaceMode(WorkspaceMode.SINGLE)
                .inferenceWorkspaceMode(WorkspaceMode.SINGLE).list()

                .layer(0, new DenseLayer.Builder().activation(Activation.RELU).nIn(nIn).nOut(nOutFF)
                        .weightInit(WeightInit.RELU).updater(AdaGrad.builder().learningRate(adaGradDense).build())
                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                        .gradientNormalizationThreshold(10).build())

                .layer(1, new DenseLayer.Builder().activation(Activation.RELU).nIn(nOutFF).nOut(nOutFF)
                        .weightInit(WeightInit.RELU).updater(AdaGrad.builder().learningRate(adaGradDense).build())
                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                        .gradientNormalizationThreshold(10).build())

                .layer(2, new DenseLayer.Builder().activation(Activation.RELU).nIn(nOutFF).nOut(nOutFF)
                        .weightInit(WeightInit.RELU).updater(AdaGrad.builder().learningRate(adaGradDense).build())
                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
                        .gradientNormalizationThreshold(10).build())

                .layer(3,
                        new GravesBidirectionalLSTM.Builder().nIn(nOutFF).nOut(lstmLayerSize)
                                .updater(AdaGrad.builder().learningRate(adaGradGraves).build())
                                .activation(Activation.SOFTSIGN).build())

                .layer(4,
                        new GravesLSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize)
                                .updater(AdaGrad.builder().learningRate(adaGradGraves).build())
                                .activation(Activation.SOFTSIGN).build())

                .layer(5, new RnnOutputLayer.Builder().activation(Activation.SIGMOID)
                        .lossFunction(LossFunctions.LossFunction.XENT).nIn(lstmLayerSize).nOut(Criterion.classifiableValues().length).build())

                .inputPreProcessor(0, new RnnToFeedForwardPreProcessor())
                .inputPreProcessor(3, new FeedForwardToRnnPreProcessor()).pretrain(false).backprop(true).build();

        // .backpropType(BackpropType.TruncatedBPTT).tBPTTForwardLength(tbpttLength).tBPTTBackwardLength(tbpttLength)

        MultiLayerNetwork net = new MultiLayerNetwork(conf);
        net.init();
        net.setListeners(new ScoreIterationListener(1));
        return net;
    }
}
