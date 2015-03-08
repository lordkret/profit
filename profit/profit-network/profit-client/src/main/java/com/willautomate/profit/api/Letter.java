package com.willautomate.profit.api;

import com.willautomate.profit.impl.BasicLetter;



public interface Letter<T> {
	int size();
	T[] getRawData();


}
