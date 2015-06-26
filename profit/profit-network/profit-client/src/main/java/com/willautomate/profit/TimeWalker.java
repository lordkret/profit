package com.willautomate.profit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;
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
		 int currentSize = 2;
		 Word word;
		 
		while (keepItGoing){
			word = WordProvider.getWord(currentSize, main);
			int letterSize = word.getLetters()[0].getRawData().length;
			network = new ElmanWordDetector(letterSize);	
			if (network.train(word)){
				Letter predicted = network.predict(WordProvider.getLetter(currentSize, main));
				Letter toPredict = WordProvider.getLetter(currentSize, main);
				wordDone = distance >= DoubleLetterDistance.calculate(toPredict, predicted, letterSize);
			}
			keepItGoing = ! wordDone && WordProvider.getMaxWordSize() > currentSize;
			if (! wordDone){
				network.clean();
				currentSize  =2;
			} else {
				currentSize++;
			}
		}
		if (wordDone) {
			
		}
		
	}

}
