package com.secureai.nn;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

public class NNBuilder {


    public FilteredMultiLayerNetwork build(int inputs, int outputs) { // default config
        return build(inputs, outputs, 3, 64, 0.001);
    }

    public FilteredMultiLayerNetwork build(int inputs, int outputs, int size, int hidden_size, double learningRate) {
        NeuralNetConfiguration.ListBuilder builder = new NeuralNetConfiguration.Builder()
                .seed(12345)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .updater(new Adam(learningRate))
                .weightInit(WeightInit.XAVIER)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(inputs)
                        .nOut(hidden_size)
                        .activation(Activation.IDENTITY)
                        .build());
        for (int i = 0; i < size; i++) {
            builder = builder.layer(new DenseLayer.Builder()
                    .nIn(hidden_size)
                    .nOut(hidden_size)
                    .activation(Activation.RELU)
                    .build());
        }
        MultiLayerConfiguration conf = builder
                .layer(new OutputLayer.Builder(LossFunction.MSE)
                        .nIn(hidden_size)
                        .nOut(outputs)
                        .activation(Activation.IDENTITY)
                        .build())
                .build();

        FilteredMultiLayerNetwork model = new FilteredMultiLayerNetwork(conf);
        model.init();
        return model;
    }
}
