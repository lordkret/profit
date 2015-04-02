package com.willautomate.profit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.neo4j.examples.server.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.willautomate.profit.api.DataConfiguration;
import com.willautomate.profit.api.WalkerConfiguration.NetworkPattern;
import com.willautomate.profit.impl.Analysis;
import com.willautomate.profit.impl.DoubleBinarizer;

public class LuckyWalker implements Runnable{
    private static final String name = "Lucky";
    private Path csv;
    WordWalker walker;
    private final NetworkPattern pattern;
    private static final Logger log = LoggerFactory.getLogger(LuckyWalker.class);
    public LuckyWalker(int i, NetworkPattern pattern) {
        this.pattern = pattern; 
    	csv = Paths.get("src/main/resources/fulldata.csv");
         walker = new WordWalker(11, 2, DataConfiguration.LetterPattern.LUCKY.toPattern())
                 .withDataFile(csv)
                 .withMaximumError(0)
                 .withStartSize(2)
                 .withMaxSize(124)
                 .saveNetwork(false)
                 .withPattern(pattern)
                 .withDistancePattern(name+i);
    }

    @Override
    public void run() {
        walker.run();
        try{
            Double[] predictedLetter = null;
                predictedLetter = DoubleBinarizer.debinarize(2, walker.uptrainAndPredict());
            int l1 = predictedLetter[0].intValue();
            int l2 = predictedLetter[1].intValue();
            
                Analysis.getInstance(name).analysis(predictedLetter);
                log.warn("Sending prediction {} {} {}",l1,l2,"jordan");
                Connector.createPrediction(0,0,0,0,0,l1,l2,walker.getWordSize(),(int)walker.getDistance(),"jordan");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }
}
