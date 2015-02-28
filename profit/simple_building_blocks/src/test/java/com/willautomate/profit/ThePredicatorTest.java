package com.willautomate.profit;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Test;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.api.WordFactory;
import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.DoubleBinarizer;

public class ThePredicatorTest {

	
	@Test
	public void predict(){
		Letter<Double> lastLetter = new BasicLetter<Double>(DoubleBinarizer.binarize(50, 2,5,18,30,43));
		ElmanWordDetector detector = new ElmanWordDetector(50);
		detector.load(Paths.get("yay1_2_distance"));
		Letter<Double> next = (Letter<Double>) detector.predict(lastLetter);
		System.out.println(Arrays.toString(DoubleBinarizer.debinarize(5, next.getRawData())));
		ElmanWordDetector detector2 = new ElmanWordDetector(50);
		detector2.load(Paths.get("yay1_2_distance_2"));
		Letter<Double> next2 = (Letter<Double>) detector2.predict(lastLetter);
		System.out.println(Arrays.toString(DoubleBinarizer.debinarize(5, next2.getRawData())));
	}
	
	@Test
	public void upTrainAndPredict() throws IOException{
		Path csv = Paths.get("src/main/resources/fulldata.csv");
        for (int no = 1; no<5;no++){
		String loc = String.format("yaypool-1-thread-%s",no);
		ElmanWordDetector detector = new ElmanWordDetector(50);
		detector.load(Paths.get(loc));
		Word p = WordFactory.fromCsv(50,csv, 1, 31, ElmanWordDetectorTest.MAIN_WORD);
        detector.train(p);
        Letter<Double> lastLetter = new BasicLetter<Double>(DoubleBinarizer.binarize(50, 3,25,28,34,50));
        Letter<Double> next = (Letter<Double>) detector.predict(lastLetter);
        Double[] d = DoubleBinarizer.debinarize(5, next.getRawData());
        Arrays.sort(d);
		System.out.println(Arrays.toString(d));
		
        }        
	}
	
	@Test
	public void upTrainLuckyAndPredict() throws IOException{
	    Path csv = Paths.get("src/main/resources/fulldata.csv");
	    ElmanWordDetector detector = new ElmanWordDetector(11);
        detector.load(Paths.get("net-final-lucky-ws-3"));
        Word p = WordFactory.fromCsv(11,csv, 1, 5, ElmanWordDetectorTest.LUCKY_WORD);
        detector.train(p);
        Letter<Double> lastLetter = new BasicLetter<Double>(DoubleBinarizer.binarize(11, 1,11));
        Letter<Double> next = (Letter<Double>) detector.predict(lastLetter);
        Double[] d = DoubleBinarizer.debinarize(2, next.getRawData());
        //Arrays.sort(d);
        System.out.println(Arrays.toString(d));
        	}
}