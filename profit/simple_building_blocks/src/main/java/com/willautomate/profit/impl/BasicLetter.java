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
		if (! ( ob instanceof BasicLetter)){
			return false;
		}
		@SuppressWarnings("unchecked")
		final Letter<Double> me = (Letter<Double>) this;
		
		@SuppressWarnings("unchecked")
		final Letter<Double> other = (Letter<Double>) ob;
		return Arrays.deepEquals(me.getRawData(), other.getRawData());
		
	}
}
