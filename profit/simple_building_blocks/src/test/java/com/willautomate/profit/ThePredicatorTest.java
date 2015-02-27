package com.willautomate.profit;

import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Test;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.DoubleBinarizer;

public class ThePredicatorTest {

	
	@Test
	public void predict(){
		Letter<Double> lastLetter = new BasicLetter<Double>(DoubleBinarizer.binarize(50, 2,5,18,30,43));
		ElmanWordDetector detector = new ElmanWordDetector();
		detector.load(Paths.get("yay1_2_distance"));
		Letter<Double> next = (Letter<Double>) detector.predict(lastLetter);
		System.out.println(Arrays.toString(DoubleBinarizer.debinarize(5, next.getRawData())));
		ElmanWordDetector detector2 = new ElmanWordDetector();
		detector2.load(Paths.get("yay1_2_distance_2"));
		Letter<Double> next2 = (Letter<Double>) detector2.predict(lastLetter);
		System.out.println(Arrays.toString(DoubleBinarizer.debinarize(5, next2.getRawData())));
	}
}
/**
07,15,24,25,49,03,07
24,25,28,39,44,05,07
4,10,14,37,46
*/