package com.willautomate.profit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.api.WordFactory;
import com.willautomate.profit.impl.DoubleBinarizer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ElmanWordDetectorTest {

    Logger log = LoggerFactory.getLogger(ElmanWordDetectorTest.class);
    @Test
    public void trainingTest() throws IOException, InterruptedException {
        ElmanWordDetector network = new ElmanWordDetector();
        int startSize = 5;
        boolean wordDone = false;
        Path csv = Paths.get("src/main/resources/fulldata.csv");
        while (!wordDone) {
            Word p = WordFactory.fromCsv(csv, 3, startSize, "M1", "M2", "M3", "M4", "M5", null, null);
            network.train(p);
            Letter<Double> toPredict = WordFactory.fromCsv(csv, 1, 1, "M1", "M2", "M3", "M4", "M5", null, null).getLetters()[0];
            Letter<Double> letterToUser = p.getLetters()[p.getLetters().length - 1];

            Letter<Double> predicted = (Letter<Double>) network.predict(letterToUser);
//            Letter<Double> predictedAgain = (Letter<Double>) network.predict(letterToUser);

            double[] predictedData = ArrayUtils.toPrimitive(DoubleBinarizer.debinarize(5, predicted.getRawData()));
//            double[] predictedAgainData = ArrayUtils.toPrimitive(DoubleBinarizer.debinarize(5, predictedAgain.getRawData()));
            
            Arrays.sort(predictedData);
//            Arrays.sort(predictedAgainData);
            wordDone = Arrays.equals(ArrayUtils.toPrimitive(DoubleBinarizer.debinarize(5, toPredict.getRawData())), predictedData);
            startSize++;
            log.info("Current word size " + startSize + " \n toPredict: " + Arrays.toString(DoubleBinarizer.debinarize(5, toPredict.getRawData())) + "\n predicted: "
                    + Arrays.toString(predictedData) + " and again ");// + Arrays.toString(predictedAgainData));
            log.info("Letter used {}",letterToUser);
            Thread.sleep(5000L);
        }
        Word predict = WordFactory.fromCsv(Paths.get("src/main/resources/fulldata.csv"), 5, 10, "M1", "M2", "M3", "M4", "M5", null, null);
        for (Letter letter : predict.getLetters()) {
            log.info("Letters: " + Arrays.deepToString(DoubleBinarizer.debinarize(5, ((Letter<Double>) letter).getRawData())) + " Predict: "
                    + Arrays.deepToString(DoubleBinarizer.debinarize(5, Arrays.copyOf(network.predict(letter).getRawData(), 50, Double[].class))));
        }
        network.save(Paths.get("yay"));

        // 1,3,31,42,46,4,11
        // 5,8,37,47,48,2,3
        // 3,15,25,44,49,1,9
        // 6,10,15,23,41,4,10
        // 3,7,25,32,36,1,6
        // 4,7,28,32,37,5,10

    }

    @Test
    public void beSoStubborn(){
        boolean meDone = false;
        int counter = 0;
        while (! meDone){
        try {
            
            wordTrainingTest();
            meDone = true;
        } catch (Throwable e){
            counter++;
            log.info("Didnt learn yet {}",counter);
        }
        log.info("Finall learned after {} times",counter);
        }
    }
    @Test
    public void wordTrainingTest() throws Exception {
        ElmanWordDetector network = new ElmanWordDetector();
        int startSize = 4;
        boolean wordDone = false;
        Path csv = Paths.get("src/main/resources/fulldata.csv");
        Word p = WordFactory.fromCsv(csv, 1, startSize, "M1", "M2", "M3", "M4", "M5", null, null);
        network.train(p);
        Letter[] letters = p.getLetters();
        for (int i=0;i<letters.length -1;i++){
            Letter l = letters[i];
            Letter<Double> predicted = (Letter<Double>) network.predict(l);
            Letter<Double> previous = l;
            Letter<Double> actual = letters[i+1];
            double[] predictedData = ArrayUtils.toPrimitive(DoubleBinarizer.debinarize(5, predicted.getRawData()));
            Arrays.sort(predictedData);
            double[] actualData = ArrayUtils.toPrimitive(DoubleBinarizer.debinarize(5, actual.getRawData()));
            log.info("Actual {} and predicted {} ",actualData,predictedData);
            assertThat(actualData, equalTo(predictedData));

        }
        
     }
}
