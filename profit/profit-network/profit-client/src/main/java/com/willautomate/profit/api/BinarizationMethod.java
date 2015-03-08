package com.willautomate.profit.api;

public interface BinarizationMethod<T> {

	T[] binarize(int bitsSize, int... data);

	T[] debinarize(int bitsSize, T... data);
}
