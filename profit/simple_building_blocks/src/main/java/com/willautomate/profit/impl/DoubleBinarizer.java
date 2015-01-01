package com.willautomate.profit.impl;

import com.google.common.base.Preconditions;
import com.willautomate.profit.api.BinarizationMethod;

public class DoubleBinarizer implements BinarizationMethod<Double>{

	public Double[] binarize(int bitsSize, int... data) {
		Double[] result = new Double[bitsSize];
		for (int value : data){
			Preconditions.checkArgument(value < bitsSize,"data value %s is out of range of binary size %s",data,bitsSize);
			result[value] = 1D;
		}
		return result;
	}

}
