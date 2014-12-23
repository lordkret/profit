package com.willautomate.profit;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.ElmanPattern;

import java.io.IOException;
import java.nio.file.Paths;

import static java.lang.Math.*;

/**
 * Created by ehanlyn on 22/12/2014.
 */
public class Elmann {

    public BasicNetwork createNetwork(int hiddenLayerSize)
    {
        ElmanPattern pattern = new ElmanPattern();
        pattern.setInputNeurons(11);
        pattern.addHiddenLayer(hiddenLayerSize);
        pattern.setOutputNeurons(11);
        pattern.setActivationFunction(new ActivationSigmoid());
        return (BasicNetwork)pattern.generate();
    }

    public static final Double MAX_ERROR = 0.008;
    public BasicNetwork train(BasicNetwork network,MLDataSet training){
        boolean finishedLearning = false;
        int fuckTor = 1;
        while (! finishedLearning) {
            network = createNetwork(training.getInputSize() * fuckTor);
            final Train train = new ResilientPropagation(network, training);
            int epoch = 1;
            boolean stuck = false;
            final int stuckEvaluator = 80;
            int stuckCount = stuckEvaluator;

            do {
                double error = train.getError();
                train.iteration();
                if (error > train.getError()){
                    stuckCount--;
                }
                if (epoch % stuckEvaluator == 0) {
                    stuck = stuckCount < stuckEvaluator / 4;
                    stuckCount = stuckEvaluator;
                }

                System.out
                        .println("Epoch #" + epoch + " Error:" + train.getError());
                epoch++;

            } while (! stuck && train.getError() > MAX_ERROR);
            finishedLearning = !stuck;
            System.out.println(network.calculateError(training));
            fuckTor++;
        }
        return network;
    }
    Binarizer ls = new Binarizer(11, 0.5, "L1","L2");
    BasicNetwork network ;
    public BasicNetwork train() throws IOException {

        return train(network,ls.binarize(Paths.get("src/main/resources/fulldataWithoutLast2.csv")));
    }

    public void evaluate() throws IOException {
        predict(network);
    }

    public void predict(BasicNetwork network) throws IOException {



        BasicNetwork regular = (BasicNetwork)network.clone();
        BasicNetwork closedLoop = (BasicNetwork)network.clone();

        regular.clearContext();
        closedLoop.clearContext();

        MLDataSet dataSet = ls.binarize(Paths.get("src/main/resources/fulldata.csv"));
        for (MLDataPair data : dataSet) {



            MLData output = regular.compute(data.getInput());

            MLData clOutput = closedLoop.compute(data.getInput());

            System.out.println(String.format("I was expecting %s, but I got %s and %s and raw meat %s", ls.deBinarize(data.getIdeal()),ls.deBinarize(output),ls.deBinarize(clOutput),output));

        }
    }
}
