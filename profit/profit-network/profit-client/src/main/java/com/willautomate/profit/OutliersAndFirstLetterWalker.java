package com.willautomate.profit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.willautomate.profit.impl.Analysis;
import com.willautomate.profit.impl.DoubleBinarizer;

public class OutliersAndFirstLetterWalker implements Runnable{
    private static final String name = "Outliers";
    private Path csv;
    WordWalker walker;
    
    public OutliersAndFirstLetterWalker(int i) {
         csv = Paths.get("src/main/resources/outliers.csv");
         walker = new WordWalker(50, 5, ElmanWordDetectorRunner.MAIN_WORD)
                 .withDataFile(csv)
                 .withMaximumError(3)
                 .withStartSize(33)
                 .withMaxSize(33)
                 .saveNetwork(false)
                 .withDistancePattern(name+i);
    }

    @Override
    public void run() {
        walker.run();
        try {
            Analysis.getInstance(name).analysis(DoubleBinarizer.debinarize(5,walker.uptrainAndPredict()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
