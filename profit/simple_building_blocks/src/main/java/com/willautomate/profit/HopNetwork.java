package com.willautomate.profit;


import com.google.common.collect.Iterables;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.specific.BiPolarNeuralData;
import org.encog.neural.thermal.HopfieldNetwork;
import org.encog.util.simple.EncogUtility;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class HopNetwork {

    HopfieldNetwork network = new HopfieldNetwork(11);
    Binarizer luckyStarBinarizer = new Binarizer(11,0.05,"L1","L2");

    public void train() throws IOException {
        final MLDataSet dataSet = luckyStarBinarizer.binarize(Paths.get("src\\test\\resources\\data.csv"));

//        network.addPattern();
    }

    public void check() {
        MLData data = new BasicMLData(Iterables.getLast(Arrays.asList(luckyStarBinarizer.getOutputData())));
        System.out.println(luckyStarBinarizer.deBinarize(data));
        System.out.println(luckyStarBinarizer.deBinarize((network).compute(data)));
    }

}
