package com.willautomate.profit.impl;

import com.willautomate.profit.api.Letter;

import java.util.Arrays;

import javax.print.attribute.standard.MediaSize.Other;

public class BasicLetter<T> implements Letter<T>{

	private final T[] rawData;
	public BasicLetter(T[] data){
		rawData = data;
	}
	
	public int size() {
		return (rawData != null)? rawData.length:0;
	}
	public T[] getRawData(){
		return rawData;
	}

	public String toString(){
		return Arrays.deepToString(rawData);
	}

	@Override
	public boolean equals(Object ob){
		final DoubleBinarizer b = new DoubleBinarizer();
		if (! ( ob instanceof BasicLetter)){
			return false;
		}
		@SuppressWarnings("unchecked")
		final Letter<Double> me = (Letter<Double>) this;

		@SuppressWarnings("unchecked")
		final Letter<Double> stuff = (Letter<Double>) ob;
		final Double[] myStuff = b.debinarize(Math.min(5, me.getRawData().length), me.getRawData());
		@SuppressWarnings("unchecked")
		Double[] br = stuff.getRawData();
		final Double[] otherStuff = b.debinarize(Math.min(5, stuff.getRawData().length),stuff.getRawData());
		System.out.println("comparing " + Arrays.deepToString(myStuff) + " and " + Arrays.deepToString(otherStuff) + " value: " + Arrays.equals(myStuff, otherStuff));
		return Arrays.equals(myStuff,otherStuff);
	}
}
