package com.willautomate.profit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.willautomate.profit.impl.Analysis;
import com.willautomate.profit.impl.DoubleBinarizer;

public class LuckyWalker implements Runnable{
    private static final String name = "Lucky";
    private Path csv;
    WordWalker walker;
    
    public LuckyWalker(int i) {
         csv = Paths.get("src/main/resources/fulldata.csv");
         walker = new WordWalker(11, 2, ElmanWordDetectorRunner.LUCKY_WORD)
                 .withDataFile(csv)
                 .withMaximumError(0)
                 .withStartSize(10)
                 .withMaxSize(20)
                 .saveNetwork(false)
                 .withDistancePattern(name+i);
    }

    @Override
    public void run() {
        walker.run();
        try {
            Analysis.getInstance(name).analysis(DoubleBinarizer.debinarize(2,walker.uptrainAndPredict()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
