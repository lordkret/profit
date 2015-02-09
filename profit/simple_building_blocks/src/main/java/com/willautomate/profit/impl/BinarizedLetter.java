package com.willautomate.profit.impl;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import com.willautomate.profit.api.Letter;

public class BinarizedLetter<T> implements Letter<T> {
	private final T[] rawData;
	public BinarizedLetter(Letter l) {
		Double[] letterData = Arrays.copyOf(l.getRawData(),l.getRawData().length, Double[].class);
		rawData = (T[]) DoubleBinarizer.binarize(50, letterData);
	}
	@Override
	public int size() {
		return (rawData == null)? 0 : rawData.length;
	}

	@Override
	public T[] getRawData() {
		return rawData;
	}

	@Override
	public boolean equals(Object o){
		if (!(o instanceof BinarizedLetter)){
			return false;
		}
		Letter<Double> me = (Letter<Double>) this;
		Letter<Double> other = (Letter<Double>) o;
		return Arrays.deepEquals(DoubleBinarizer.debinarize(Math.min(5,me.size()),me.getRawData()), DoubleBinarizer.debinarize(Math.min(5,other.size()),other.getRawData()));
		
	}
}
