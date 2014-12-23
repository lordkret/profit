package com.willautomate.profit;

import com.google.common.collect.Iterables;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.util.simple.EncogUtility;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class FirstNetwork {

    MLMethodFactory methodFactory = new MLMethodFactory();
    MLMethod method = methodFactory.create(MLMethodFactory.TYPE_RBFNETWORK,"?->gaussian(c=60)->?",11,11);
    LuckyStarBinarizer luckyStarBinarizer = new LuckyStarBinarizer();

    public void train() throws IOException {
        EncogUtility.trainToError(method,luckyStarBinarizer.binarize(Paths.get("src\\test\\resources\\data.csv")),0.00005);
    }

    public void check() throws IOException {
        MLData data = new BasicMLData(Iterables.getLast(Arrays.asList(luckyStarBinarizer.getOutputData())));
        System.out.println(luckyStarBinarizer.deBinarize(data));
        System.out.println(luckyStarBinarizer.deBinarize(((org.encog.ml.MLRegression) method).compute(data)));
//        EncogUtility.evaluate((org.encog.ml.MLRegression) method,luckyStarBinarizer.binarize(Paths.get("src\\test\\resources\\data2.csv")));
    }
}
