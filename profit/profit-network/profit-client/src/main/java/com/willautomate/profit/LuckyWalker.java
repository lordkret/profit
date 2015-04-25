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
	private final int startSize ;
	private final int maxSize ;
	private final int distancePatternQuantifier;
	public LuckyWalker(int i, NetworkPattern pattern,int startSize, int maxSize){
		this.pattern = pattern;
		this.startSize = startSize;
		this.maxSize = maxSize;
		this.distancePatternQuantifier = i;
		configureWalker();
	}
	private final void configureWalker(){
		csv = Paths.get("src/main/resources/fulldata.csv");
		walker = new WordWalker(11, 2, DataConfiguration.LetterPattern.LUCKY.toPattern())
		.withDataFile(csv)
		.withMaximumError(0)
		.withStartSize(startSize)
		.withMaxSize(maxSize)
		.saveNetwork(true)
		.withPattern(pattern)
		.withDistancePattern(name+distancePatternQuantifier);

	}
	public LuckyWalker(int i, NetworkPattern pattern) {
		this.startSize = 5;
		this.maxSize = 126;
		this.pattern = pattern; 
		this.distancePatternQuantifier = i;
		configureWalker();
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
			log.warn("Sending prediction {} {} {} {} {}",l1,l2,"jordan",walker.getWeightValue(),walker.isSmart());
			Connector.createPrediction(0,0,0,0,0,l1,l2,walker.getWordSize(),(int)walker.getDistance(),"jordan",walker.getWeightValue(),walker.isSmart());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
