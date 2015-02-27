package com.willautomate.profit.impl;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.willautomate.profit.api.Letter;

public class DoubleLetterDistance {
	
	private static Logger log = LoggerFactory.getLogger(DoubleLetterDistance.class);

	public static double calculate(Letter a, Letter b, int comparizonSize){
		
		
		Double[] aData = DoubleBinarizer.debinarize(comparizonSize,((Letter<Double>) a).getRawData());
		Arrays.sort(aData);
		Double[] bData = DoubleBinarizer.debinarize(comparizonSize,((Letter<Double>) b).getRawData());
		Arrays.sort(bData);
		
		double distance = 0;
		for (int i=0; i<comparizonSize;i++){
			if (bData[i] - aData[i] != 0)
				distance ++;
		}
		
		log.debug("Comparing {} to {}. Distance {}",Arrays.toString(aData),Arrays.toString(bData),distance);
		return distance;
	}
}
