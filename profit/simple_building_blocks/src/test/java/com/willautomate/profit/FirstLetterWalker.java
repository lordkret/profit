package com.willautomate.profit;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FirstLetterWalker  implements Runnable {
	
	private Path csv;
	WordWalker walker;
	
	public FirstLetterWalker() {
		 csv = Paths.get("src/main/resources/firstLetters.csv");
		 walker = new WordWalker(50, 5, ElmanWordDetectorTest.MAIN_WORD)
				 .withDataFile(csv)
				 .withMaximumError(0)
				 .withStartSize(17)
				 .withMaxSize(17)
				 .withDistancePattern("FirstLetter");
	}

	@Override
	public void run() {
		walker.run();
	}
}
