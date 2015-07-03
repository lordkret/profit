package com.willautomate.profit;

import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.DoubleBinarizer;
import com.willautomate.profit.impl.DoubleLetterDistance;

public class TimeWalker implements Runnable {
	
	private ElmanWordDetector network;
	private static final Logger log = LoggerFactory.getLogger(TimeWalker.class);
	private boolean main = false;
	private double distance = 0;
	private int maxWordSize = WordProvider.getMaxWordSize();
	private static final int DEFAULT_BEGIN = WordProvider.getMaxWordSize();	
	@Override
	public void run() {
		 boolean keepItGoing = true;
		 boolean wordDone = false;
		 int currentSize = DEFAULT_BEGIN;
		 int debinarizedLetterSize = (main)?5:2;
		 Word word;
		 
		while (keepItGoing){
			log.info("Fetching word with size {}",currentSize);
			word = WordProvider.getWord(currentSize, main);
			int letterSize = word.getLetters()[0].getRawData().length;
			log.info("Size of the letter {} ",letterSize);
			network = new ElmanWordDetector(debinarizedLetterSize);	
			if (network.train(word)){
				log.warn("Training with word size {} finished",currentSize);
				Letter<Double> predicted = (Letter<Double>) network.predict(WordProvider.getLetter(currentSize, main));
				log.warn("Predicted letter {} or {}",DoubleBinarizer.debinarize(debinarizedLetterSize, predicted.getRawData()),predicted);
				Letter<Double> toPredict = WordProvider.getLetter(currentSize+1, main);
				try {
				double calculatedDistance = DoubleLetterDistance.calculate(toPredict,predicted, debinarizedLetterSize);
				wordDone = distance >= calculatedDistance;
				log.warn("Distance to {} is {}", DoubleBinarizer.debinarize(debinarizedLetterSize,toPredict.getRawData()),calculatedDistance);
				} catch (IllegalArgumentException sit){
					log.info("this network produced strange letter");
					wordDone = false;
				}
			}
			keepItGoing = maxWordSize > currentSize;
			if (! wordDone){
				log.warn("This network seems to be useless, restarting");
				network.clean();
				currentSize=DEFAULT_BEGIN;
			} else {
				currentSize++;
			}
		}
		if (wordDone) {
			String netName = String.format("good-%s-%s.network", distance,System.currentTimeMillis());
			log.info("Got good network {}" , netName);
			network.save(Paths.get(netName));
		}
		
	}

}
