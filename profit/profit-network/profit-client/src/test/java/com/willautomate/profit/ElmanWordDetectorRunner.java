package com.willautomate.profit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;
import org.encog.Encog;
import org.encog.plugin.system.SystemLoggingPlugin;
import org.encog.util.logging.EncogLogging;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.WalkerConfiguration.NetworkPattern;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.api.WordFactory;
import com.willautomate.profit.impl.DoubleBinarizer;

public class ElmanWordDetectorRunner {

    private static final Logger log = LoggerFactory.getLogger(ElmanWordDetector.class);

    public static final String[] MAIN_WORD = {"M1", "M2", "M3", "M4", "M5", null, null};
    public static final String[] LUCKY_WORD = {null,null,null,null,null,"L1","L2"};
   
    @Test
    public void threeStream() throws InterruptedException{
//    	SystemLoggingPlugin logging = (SystemLoggingPlugin)Encog.getInstance().getLoggingPlugin();
//    	logging.setLogLevel(EncogLogging.LEVEL_DEBUG);
//    	logging.startConsoleLogging();
        ExecutorService executor = Executors.newFixedThreadPool(2);
//        executor.execute(new FullDataWordWalker(101));
        for (int i = 0; i < 1; i++) {
            executor.execute(new LuckyWalker(i,NetworkPattern.ElmannStep));
            executor.execute(new LuckyWalker(10+i,NetworkPattern.Elmann));
//            executor.execute(new GrowingWordWalker(i,2));
//            executor.execute(new GrowingWordWalker(i,3));
//            executor.execute(new GrowingWordWalker(i,1));
        }
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.HOURS);
    }

//    @Test
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
