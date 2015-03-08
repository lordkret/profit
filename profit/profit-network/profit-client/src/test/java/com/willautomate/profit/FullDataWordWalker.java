package com.willautomate.profit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.willautomate.profit.impl.Analysis;
import com.willautomate.profit.impl.DoubleBinarizer;

public class FullDataWordWalker implements Runnable{
    private static final String name = "FullData";
    private Path csv;
    WordWalker walker;
    
    public FullDataWordWalker(int i) {
         csv = Paths.get("src/main/resources/fulldata.csv");
         walker = new WordWalker(50, 5, ElmanWordDetectorRunner.MAIN_WORD)
                 .withDataFile(csv)
                 .withMaximumError(5)
                 .withStartSize(121)
                 .withMaxSize(121)
                 .saveNetwork(true)
                 .withDistancePattern(name+i);
    }

    @Override
    public void run() {
        walker.run();
        try {
            Analysis.getInstance(name).analysis(DoubleBinarizer.debinarize(5, walker.uptrainAndPredict()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
