package com.willautomate.profit;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.api.WordFactory;
import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.BasicWord;
import com.willautomate.profit.impl.BinarizedLetter;
import com.willautomate.profit.impl.DoubleBinarizer;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ElmanWordDetectorTest {

    @Test
    public void trainingTest() throws IOException, InterruptedException {
        ElmanWordDetector network = new ElmanWordDetector();
        int startSize = 5;
        boolean wordDone = false;
        Path csv = Paths.get("src/main/resources/fulldata.csv");
        while (! wordDone) {
            Word p = WordFactory.fromCsv(csv, 1, startSize, "M1", "M2", "M3", "M4", "M5", null, null);
            network.train(p);
            Letter<Double> toPredict = WordFactory.fromCsv(csv,startSize+1, startSize +1,"M1", "M2", "M3", "M4", "M5", null, null).getLetters()[0];
            Letter<Double> predicted = (Letter<Double>)network.predict(p.getLetters()[p.getLetters().length-1]);
            double[] predictedData = ArrayUtils.toPrimitive(DoubleBinarizer.debinarize(5, predicted.getRawData()));
            Arrays.sort(predictedData);
            System.out.println("s " + Arrays.toString(predictedData));
            wordDone = Arrays.equals(ArrayUtils.toPrimitive(DoubleBinarizer.debinarize(5, toPredict.getRawData())), predictedData);
            startSize++;
            System.out.println("Current word size " + startSize + " \n toPredict: " + Arrays.toString(DoubleBinarizer.debinarize(5, toPredict.getRawData())) + "\n predicted: " + Arrays.toString(predictedData));

            Thread.sleep(5000L);
        }
        Word predict = WordFactory.fromCsv(Paths.get("src/main/resources/fulldata.csv"), 5, 10, "M1","M2","M3","M4","M5",null,null);
        for(Letter letter: predict.getLetters()){
            System.out.println("Letters: " + Arrays.deepToString(DoubleBinarizer.debinarize(5,((Letter<Double>) letter).getRawData())) + " Predict: " + Arrays.deepToString(DoubleBinarizer.debinarize(5,Arrays.copyOf(network.predict(letter).getRawData(), 50, Double[].class))));
        }
        network.save(Paths.get("yay"));

//        1,3,31,42,46,4,11
//        5,8,37,47,48,2,3
//        3,15,25,44,49,1,9
//        6,10,15,23,41,4,10
//        3,7,25,32,36,1,6
//        4,7,28,32,37,5,10


    }
}
