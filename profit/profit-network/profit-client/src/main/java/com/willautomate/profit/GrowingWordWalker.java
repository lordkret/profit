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

public class GrowingWordWalker implements Runnable{
	private static final Logger log = LoggerFactory.getLogger(GrowingWordWalker.class);
	private static final String name = "GrowingWord";
	private Path csv;
	WordWalker walker;
	private final NetworkPattern pattern;
	public GrowingWordWalker(int i,NetworkPattern pattern){
		this(i,1,pattern);
	}
	private final int startSize ;
	private final int maxSize ;
	private final int distancePatternQuantifier;
	private final int error;
	public GrowingWordWalker(int i, int error, NetworkPattern pattern,int startSize, int maxSize){
		this.pattern = pattern;
		this.startSize = startSize;
		this.maxSize = maxSize;
		this.error=error;
		this.distancePatternQuantifier = i;
		configureWalker();
	}
	private final void configureWalker(){
		csv = Paths.get("src/main/resources/fulldata.csv");
		walker = new WordWalker(50, 5, DataConfiguration.LetterPattern.MAIN.toPattern())
		.withDataFile(csv)
		.withMaximumError(error)
		.withStartSize(startSize)
		.withMaxSize(maxSize)
		.saveNetwork(true)
		.withPattern(pattern)
		.withDistancePattern(name+distancePatternQuantifier);

	}
	public GrowingWordWalker(int i, int error, NetworkPattern pattern) {
		this.startSize = 30;
		this.maxSize = 43;
		this.pattern = pattern; 
		this.distancePatternQuantifier = i;
		this.error=error;
		configureWalker();
	}

	@Override
	public void run() {
		walker.run();
		try{
		Double[] predictedLetter = null;
            predictedLetter = DoubleBinarizer.debinarize(5, walker.uptrainAndPredict());
    	int m1 = predictedLetter[0].intValue();
		int m2 = predictedLetter[1].intValue();
		int m3 = predictedLetter[2].intValue();
		int m4 = predictedLetter[3].intValue();
		int m5 = predictedLetter[4].intValue();
		
            Analysis.getInstance(name).analysis(predictedLetter);
            log.warn("Senging prediction {} {} {} {} {} {} {}",m1,m2,m3,m4,m5,"jordan",walker.isSmart());
            Connector.createPrediction(m1,m2,m3,m4,m5,0,0,walker.getWordSize(),(int)walker.getDistance(),"jordan",walker.getWeightValue(),walker.isSmart());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
}
