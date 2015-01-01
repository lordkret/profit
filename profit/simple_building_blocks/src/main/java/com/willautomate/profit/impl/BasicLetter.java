package com.willautomate.profit.impl;

import java.util.Arrays;

import com.willautomate.profit.api.Letter;

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
}
