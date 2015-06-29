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
	private static final Logger log = LoggerFactory.getLogger(WordWalker.class);
	private boolean main = true;
	private double distance = 0;
	@Override
	public void run() {
		 boolean keepItGoing = true;
		 boolean wordDone = false;
		 int currentSize = 4;
		 Word word;
		 
		while (keepItGoing){
			log.info("Fetching word with size {}",currentSize);
			word = WordProvider.getWord(currentSize, main);
			int letterSize = word.getLetters()[0].getRawData().length;
			log.info("Size of the letter {} ",letterSize);
			network = new ElmanWordDetector(5);	
			if (network.train(word)){
				log.info("Training finished");
				Letter<Double> predicted = (Letter<Double>) network.predict(WordProvider.getLetter(currentSize, main));
				log.info("Predicted letter {} or {}",DoubleBinarizer.debinarize(5, predicted.getRawData()),predicted);
				Letter<Double> toPredict = WordProvider.getLetter(currentSize+1, main);
				double calculatedDistance = DoubleLetterDistance.calculate(toPredict,predicted, 5);
				wordDone = distance >= calculatedDistance;
				log.info("Distance to {} is {}", toPredict,calculatedDistance);
			}
			keepItGoing = ! wordDone && WordProvider.getMaxWordSize() > currentSize;
			if (! wordDone){
				log.info("This network seems to be useless, restarting");
				network.clean();
				currentSize=4;
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
