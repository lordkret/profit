package com.willautomate.profit;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationStep;
import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.NeuralNetworkPattern;
import org.encog.neural.pattern.PatternError;

public class ElmannPatternStep implements NeuralNetworkPattern {
    /**
     * The number of input neurons.
     */
    private int inputNeurons;

    /**
     * The number of output neurons.
     */
    private int outputNeurons;

    /**
     * The number of hidden neurons.
     */
    private int hiddenNeurons;

    /**
     * The activation function.
     */
    private ActivationFunction activation;

    /**
     * Create an object to generate Elman neural networks.
     */
    public ElmannPatternStep() {
        this.inputNeurons = -1;
        this.outputNeurons = -1;
        this.hiddenNeurons = -1;
    }

    /**
     * Add a hidden layer with the specified number of neurons.
     * 
     * @param count
     *            The number of neurons in this hidden layer.
     */
    @Override
    public void addHiddenLayer(final int count) {
        if (this.hiddenNeurons != -1) {
            throw new PatternError(
                    "An Elman neural network should have only one hidden layer.");
        }

        this.hiddenNeurons = count;

    }

    /**
     * Clear out any hidden neurons.
     */
    @Override
    public void clear() {
        this.hiddenNeurons = -1;
    }

    /**
     * Generate the Elman neural network.
     * 
     * @return The Elman neural network.
     */
    @Override
    public MLMethod generate() {
        BasicLayer hidden, input;

        final BasicNetwork network = new BasicNetwork();
        network.addLayer(input = new BasicLayer(this.activation, true,
                this.inputNeurons));
        network.addLayer(hidden = new BasicLayer(this.activation, true,
                this.hiddenNeurons));
        network.addLayer(new BasicLayer(new ActivationStep(), false, this.outputNeurons));
        
        input.setContextFedBy(input);
        network.getStructure().finalizeStructure();
        network.reset();
        return network;
    }

    /**
     * Set the activation function to use on each of the layers.
     * 
     * @param activation
     *            The activation function.
     */
    @Override
    public void setActivationFunction(final ActivationFunction activation) {
        this.activation = activation;
    }

    /**
     * Set the number of input neurons.
     * 
     * @param count
     *            Neuron count.
     */
    @Override
    public void setInputNeurons(final int count) {
        this.inputNeurons = count;
    }

    /**
     * Set the number of output neurons.
     * 
     * @param count
     *            Neuron count.
     */
    @Override
    public void setOutputNeurons(final int count) {
        this.outputNeurons = count;
    }
}
