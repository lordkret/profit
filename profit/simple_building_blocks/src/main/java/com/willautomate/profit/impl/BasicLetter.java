package com.willautomate.profit.impl;

import com.willautomate.profit.api.Letter;

import java.util.Arrays;

public class BasicLetter<T> implements Letter<T>{

	private final T[] rawData;
	public BasicLetter(T[] data){
		rawData = data;
	}
	
	public int size() {
		return (rawData != null)? rawData.length:0;
	}
	public T[] getRawData(){
		return Arrays.copyOf(rawData, size());
	}

	public String toString(){
		return Arrays.deepToString(rawData);
	}

	public boolean equals(BasicLetter ob){
		DoubleBinarizer b = new DoubleBinarizer();
		Letter<Double> me = (Letter<Double>) this;

		Double[] myStuff = b.debinarize(5, me.getRawData());
		Double[] otherStuff = b.debinarize(5,((Letter<Double>)ob).getRawData());
		return Arrays.equals(myStuff,otherStuff);
	}
}
