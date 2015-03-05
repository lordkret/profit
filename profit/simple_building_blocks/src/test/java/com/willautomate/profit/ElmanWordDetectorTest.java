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
import java.util.concurrent.TimeUnit;

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

    public static final String[] MAIN_WORD = {"M1", "M2", "M3", "M4", "M5", null, null};
    public static final String[] LUCKY_WORD = {null,null,null,null,null,"L1","L2"};
    @Test
    public void trainingTest() throws IOException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(36);
        for (int i = 0; i < 40; i++) {
            Runnable worker = new WordWalker(50,5,MAIN_WORD).withStartSize(15).withMaximumError(1).withMaxSize(60).withDistancePattern("main-"+i);
            executor.execute(worker);
            executor.execute(new WordWalker(11, 2, LUCKY_WORD).withStartSize(1).withMaxSize(30).withMaximumError(0).withDistancePattern("lucky-"+i));
            
            if (i%3 == 0){
                executor.execute(new WordWalker(50,5,MAIN_WORD).withMaximumError(1).withStartSize(1).withMaxSize(120).withDistancePattern("mainlong"+i));
                executor.execute(new WordWalker(50,5,MAIN_WORD).withStartSize(29).withMaximumError(0).withMaxSize(36).withDistancePattern("main0-"+i));
            }
        }
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.DAYS);
    }

    @Test
    public void threeStream() throws InterruptedException{
        ExecutorService executor = Executors.newFixedThreadPool(48);
        for (int i = 0; i < 100; i++) {
        executor.execute(new FirstLetterWalker(i));
        executor.execute(new OutliersAndFirstLetterWalker(i));
        executor.execute(new FullDataWordWalker(i));
        executor.execute(new WordWalker(11, 2, LUCKY_WORD).withStartSize(10).withMaxSize(20).withMaximumError(0).withDistancePattern("lucky-"+i));
        
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
    }
    
    @Test
    public void wordTrainingTest() throws Exception {
        ElmanWordDetector network = new ElmanWordDetector(50);
        int startSize = 4;
        boolean wordDone = false;
        Path csv = Paths.get("src/main/resources/fulldata.csv");
        Word p = WordFactory.fromCsv(50,csv, 1, startSize, "M1", "M2", "M3", "M4", "M5", null, null);
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
    

   
}
