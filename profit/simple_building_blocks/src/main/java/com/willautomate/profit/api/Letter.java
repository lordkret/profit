package com.willautomate.profit.api;



public interface Letter<T> {
	int size();
	T[] getRawData();
}
