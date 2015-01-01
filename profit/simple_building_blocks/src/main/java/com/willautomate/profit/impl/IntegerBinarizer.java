package com.willautomate.profit.impl;

import com.google.common.base.Preconditions;
import com.willautomate.profit.api.BinarizationMethod;

public class IntegerBinarizer implements BinarizationMethod<Integer>{

	public Integer[] binarize(int bitsSize, int... data) {
		Integer[] result = new Integer[bitsSize];
		
		for (int value : data){
			Preconditions.checkArgument(value < bitsSize,"data value %s is out of range of binary size %s",data,bitsSize);
			result[value] = 1;
		}
		return result;
		
	}
	
}
