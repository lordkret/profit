package com.willautomate.profit;

import java.util.Arrays;

import com.google.common.util.concurrent.AtomicDouble;

public class WeightsDispatcher {
	
	private static final double INITIAL_VALUE = -1;
	private static final double STEP = 0.1;
	private static AtomicDouble weightValue = new AtomicDouble(INITIAL_VALUE);
	
	
	public static synchronized double[] weights(final int size){
		double[] result = new double[size];
		Arrays.fill(result, getWeightValue());
		return result;
	}

	private static double getWeightValue(){
		if (weightValue.get() >= 1){
			weightValue.set(INITIAL_VALUE);
			return weightValue.get();
		} else {
			return weightValue.addAndGet(STEP);
		}
	}
}
