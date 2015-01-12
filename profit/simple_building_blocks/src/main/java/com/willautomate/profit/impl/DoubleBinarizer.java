package com.willautomate.profit.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.willautomate.profit.api.BinarizationMethod;

import java.util.Arrays;
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
		System.out.println(Arrays.deepToString(data));
		for(int i=0; i<data.length; i++){

			if(isAboveThreshold(data[i])){
				result.add((double) i);
			}
		}
		return result.toArray(new Double[result.size()]);
	}

}
