package com.willautomate.profit.impl;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import com.willautomate.profit.api.Letter;

public class BinarizedLetter<T> implements Letter<T> {
	private final T[] rawData;
	public BinarizedLetter(Letter l) {
		Integer[] letterData = Arrays.copyOf(l.getRawData(),l.getRawData().length, Integer[].class);
		rawData = (T[]) DoubleBinarizer.binarize(50, ArrayUtils.toPrimitive(letterData));
	}
	@Override
	public int size() {
		return (rawData == null)? 0 : rawData.length;
	}

	@Override
	public T[] getRawData() {
		return rawData;
	}

}
