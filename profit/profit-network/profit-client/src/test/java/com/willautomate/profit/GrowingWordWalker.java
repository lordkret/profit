package com.willautomate.profit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.willautomate.profit.impl.Analysis;
import com.willautomate.profit.impl.DoubleBinarizer;

public class GrowingWordWalker implements Runnable{
	private static final String name = "GrowingWord";
	private Path csv;
	WordWalker walker;
	
	public GrowingWordWalker(int i){
		this(i,1);
	}
	public GrowingWordWalker(int i, int error) {
		 csv = Paths.get("src/main/resources/fulldata.csv");
		 walker = new WordWalker(50, 5, ElmanWordDetectorTest.MAIN_WORD)
				 .withDataFile(csv)
				 .withMaximumError(error)
				 .withStartSize(20)
				 .withMaxSize(40)
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
