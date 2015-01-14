package com.willautomate.profit.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.willautomate.profit.api.BinarizationMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DoubleBinarizer implements BinarizationMethod<Double>{

	public Double[] binarize(int bitsSize, int... data) {
		Double[] result = new Double[bitsSize];
		Arrays.fill(result,0D);
		for (int value : data){
			Preconditions.checkArgument(value < bitsSize,"data value %s is out of range of binary size %s",data,bitsSize);
			result[value] = 1D;
		}
		return result;
	}

	private boolean isAboveThreshold(Double value){
		return value>0.8;
	}
	@Override
	public Double[] debinarize(int bitsSize, Double... data) {
		List<Double> result = Lists.newArrayList();

		Double[] sorted = Arrays.copyOf(data,data.length);

		Collections.sort(Arrays.asList(sorted),Collections.reverseOrder());
		for(int i=0; i<bitsSize; i++){
			result.add((double) Arrays.asList(data).indexOf(sorted[i]));

		}
		Collections.sort(result);
		return result.toArray(new Double[result.size()]);
	}

}
