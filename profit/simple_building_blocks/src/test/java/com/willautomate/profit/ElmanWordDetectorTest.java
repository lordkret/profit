package com.willautomate.profit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.api.WordFactory;
import com.willautomate.profit.impl.DoubleBinarizer;
import com.willautomate.profit.impl.DoubleLetterDistance;

public class ElmanWordDetectorTest {

    private static final Logger log = LoggerFactory.getLogger(ElmanWordDetectorTest.class);

    @Test
    public void trainingTest() throws IOException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            Runnable worker = new WordWalker();
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {

        }

    }

    @Test
    public void beSoStubborn() {
        boolean meDone = false;
        int counter = 0;
        while (!meDone) {
            try {

                wordTrainingTest();
                meDone = true;
            } catch (Throwable e) {
                counter++;
                log.info("Didnt learn yet {}", counter);
            }
            log.info("Finall learned after {} times", counter);
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
        for (int i = 0; i < letters.length - 1; i++) {
            Letter l = letters[i];
            Letter<Double> predicted = (Letter<Double>) network.predict(l);
            Letter<Double> previous = l;
            Letter<Double> actual = letters[i + 1];
            double[] predictedData = ArrayUtils.toPrimitive(DoubleBinarizer.debinarize(5, predicted.getRawData()));
            Arrays.sort(predictedData);
            double[] actualData = ArrayUtils.toPrimitive(DoubleBinarizer.debinarize(5, actual.getRawData()));
            log.info("Actual {} and predicted {} ", actualData, predictedData);
            assertThat(actualData, equalTo(predictedData));

        }

    }

    private static class WordWalker implements Runnable {

        @Override
        public void run() {
            try {
                ElmanWordDetector network = new ElmanWordDetector();
                // int maximumWordSize = 50;
                Path csv = Paths.get("src/main/resources/fulldata.csv");
                Path profitDistance = Paths.get("profitDistance"+Thread.currentThread().getName());
                if (!Files.exists(profitDistance))
                    Files.createFile(profitDistance);
                
                Path minimalDistanceF = Paths.get("minimalDistance");
                if (!Files.exists(minimalDistanceF))
                    Files.createFile(minimalDistanceF);
                
                for (int samples = 0; samples < 1; samples++) {
                    int startSize = 21;
                    int maxSize = 35;
                    boolean wordDone = false;
                    StringBuilder builder = new StringBuilder();
                    log.warn("Starting cycle {}", samples);
                    double minimalDistance = 10;
                    int wordSize = 0;
                    while (!wordDone) {
                        Word p = WordFactory.fromCsv(csv, 3, startSize, "M1", "M2", "M3", "M4", "M5", null, null);
                        network.clean();
                        network.train(p);
                        Letter<Double> toPredict = WordFactory.fromCsv(csv, 1, 1, "M1", "M2", "M3", "M4", "M5", null, null).getLetters()[0];
                        Letter<Double> letterToUser = p.getLetters()[p.getLetters().length - 1];

                        Letter<Double> predicted = (Letter<Double>) network.predict(letterToUser);

                        double distance = DoubleLetterDistance.calculate(toPredict, predicted, 5);
                        double[] predictedData = ArrayUtils.toPrimitive(DoubleBinarizer.debinarize(5, predicted.getRawData()));
                        if (distance < minimalDistance) {
                            minimalDistance = distance;
                            wordSize = startSize;
                            log.warn("Current minimal distance {} and word size {}", minimalDistance, wordSize);
                        }
                        wordDone = distance < 3;
                        Arrays.sort(predictedData);

                        builder.append(distance + ",");
                        if (startSize < maxSize){
                        	startSize++;
                        } else {
                        	startSize = 21;
                        }
                        log.warn("Current word size " + startSize + " \n toPredict: " + Arrays.toString(DoubleBinarizer.debinarize(5, toPredict.getRawData())) + "\n predicted: "
                                + Arrays.toString(predictedData) + " and distance: " + distance);
                        log.info("Letter used {}", letterToUser);
                    }
                    builder.append("\n");
                    log.warn("Writing {} to file", builder.toString());
                    Files.write(profitDistance, builder.toString().getBytes(), StandardOpenOption.APPEND);
                    Files.write(minimalDistanceF, String.format("%s,%s", minimalDistance,wordSize).getBytes(),StandardOpenOption.APPEND);
                }
                network.save(Paths.get("yay" + Thread.currentThread().getName()));
            } catch (Exception e) {
                log.error("Issue occured", e);
            }
        }
    }
}
