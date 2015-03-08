package com.willautomate.profit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.willautomate.profit.impl.Analysis;
import com.willautomate.profit.impl.DoubleBinarizer;

public class FirstLetterWalker  implements Runnable {
	private static final String name = "FirstLetter";
	private Path csv;
	WordWalker walker;
	
	public FirstLetterWalker(int i) {
		 csv = Paths.get("src/main/resources/firstLetters.csv");
		 walker = new WordWalker(50, 5, ElmanWordDetectorTest.MAIN_WORD)
				 .withDataFile(csv)
				 .withMaximumError(3)
				 .withStartSize(16)
				 .withMaxSize(16)
				 .saveNetwork(false)
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
