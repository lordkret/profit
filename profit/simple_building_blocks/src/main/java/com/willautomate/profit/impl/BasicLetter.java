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
		final Letter<Double> other = (Letter<Double>) ob;
		Double[] mePreBinarized = me.getRawData();
		Double[] otherPreBinarized = other.getRawData();
		System.out.println(String.format("Pre %s %s", Arrays.deepToString(mePreBinarized),Arrays.deepToString(otherPreBinarized)));
		final Double[] myStuff = b.debinarize(Math.min(5, mePreBinarized.length), mePreBinarized);
		
		final Double[] otherStuff = b.debinarize(Math.min(5, otherPreBinarized.length),otherPreBinarized);
		System.out.println(String.format("For letter %s got data %s and for %s got data %s",me,Arrays.deepToString(myStuff),other,Arrays.deepToString(otherStuff)));
		System.out.println("comparing " + Arrays.deepToString(myStuff) + " and " + Arrays.deepToString(otherStuff) + " value: " + Arrays.equals(myStuff, otherStuff));
		return Arrays.equals(myStuff,otherStuff);
	}
}
