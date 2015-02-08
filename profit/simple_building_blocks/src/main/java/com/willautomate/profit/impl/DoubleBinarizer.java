package com.willautomate.profit.impl;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.willautomate.profit.api.BinarizationMethod;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.Map.Entry;

public class DoubleBinarizer implements BinarizationMethod<Double> {

	public Double[] binarize(int bitsSize, Collection<String> collection) {
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

	public static List<Double> sortByValue(Map<Integer, Double> map) {
		List<Map.Entry<Integer, Double>> list = new LinkedList<>(map.entrySet());
		System.out.println("List pres " + list);
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		System.out.println("List post " + list);
		List<Double> result = Lists.newArrayList();
		for (Map.Entry<Integer, Double> entry : list ){
			result.add(Double.valueOf(entry.getKey()));
		}
		return result;
	}

	
	public Double[] debinarize(int bitsSize, double... data){
		return debinarize(bitsSize,ArrayUtils.toObject(data));
	}
	public Double[] debinarize(int bitsSize, Double... data) {
		List<Double> result = Lists.newArrayList();
		SortedMap<Integer, Double> maxValues = Maps.newTreeMap();
		for (int i = 1; i<=data.length;i++){
			maxValues.put(i,data[i-1]);
		}

		result = sortByValue(maxValues).subList(0, bitsSize);
		System.out.println("Lamr: " + result);
		return result.toArray(new Double[bitsSize]);
	}

	public Double[] binarize(int bitsSize, int... data) {
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