package com.willautomate.profit.impl;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.willautomate.profit.api.Letter;

public class DoubleLetterDistance {
	
	private static Logger log = LoggerFactory.getLogger(DoubleLetterDistance.class);

	public static double calculate(Letter a, Letter b, int comparizonSize){
		Double[] aData = DoubleBinarizer.debinarize(comparizonSize,((Letter<Double>) a).getRawData());
		Arrays.sort(aData);
		Double[] bData = DoubleBinarizer.debinarize(comparizonSize,((Letter<Double>) b).getRawData());
		Arrays.sort(bData);
		log.info("Comparing {} to {}",aData,bData);
		double distance = 0;
		boolean doIncrease = true;
		for (int i=0; i<comparizonSize;i++){
			doIncrease = true;
			for (int j =0 ; j< comparizonSize; j++){
				if (bData[i] - aData[j] == 0){
					doIncrease = false;
				}
			}
			if (doIncrease) distance++;		
		}
		log.trace("Comparing {} to {}. Distance {}",Arrays.toString(aData),Arrays.toString(bData),distance);
		return distance;
	}
	
	public static List<Integer> calculate(double[] a, double[] b){
		List<Integer> result = Lists.newArrayList();
		for (int i=0;i<a.length;i++){
			if (a[i] != b[i]){
				result.add(i);
			}
		}
		return result;
	}
}
