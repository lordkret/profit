package com.willautomate.profit.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.willautomate.profit.api.BinarizationMethod;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.Map.Entry;

public class DoubleBinarizer  {

	public static Double[] binarize(int bitsSize, Collection<String> collection) {
		Double[] result = new Double[bitsSize];
		Arrays.fill(result, 0D);
		for (String value : collection) {
			int intValue = (Integer) ConvertUtils.convert(value, Integer.class);
			Preconditions.checkArgument(intValue <= bitsSize,
					"data value %s is out of range of binary size %s",
					collection, bitsSize);
			result[intValue -1] = 1D;
		}
		return result;
	}

	private boolean isAboveThreshold(Double value) {
		return value > 0.8;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(
			Map<K, V> map) {
		List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		
		Map<K, V> result = new LinkedHashMap<>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	public static Double[] debinarize(int bitsSize, double... data){
		return debinarize(bitsSize,ArrayUtils.toObject(data));
	}
	public static Double[] debinarize(int bitsSize, Double... data) {
		List<Double> result = Lists.newArrayList();
		Map<Integer, Double> maxValues = Maps.newHashMap();
		for (int i = 1; i<=data.length;i++){
			maxValues.put(i,data[i-1]);
		}
		maxValues = sortByValue(maxValues);
		for (Entry<Integer, Double> d : maxValues.entrySet()){
			result.add(Double.valueOf(d.getKey()));
		}
		result =result.subList(0, bitsSize);
		Collections.sort(result);
		return result.toArray(new Double[bitsSize]);
	}

	public static Double[] binarize(int bitsSize, int... data) {
		Double[] result = new Double[bitsSize];
		Arrays.fill(result, 0D);
		for (int value : data) {
			Preconditions.checkArgument(value <= bitsSize,
					"data value %s is out of range of binary size %s", data,
					bitsSize);
			result[value - 1] = 1D;
		}
		return result;
	}
}