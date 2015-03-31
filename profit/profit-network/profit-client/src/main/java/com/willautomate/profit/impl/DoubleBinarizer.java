package com.willautomate.profit.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.willautomate.profit.api.Letter;

public class DoubleBinarizer  {
	private static final Logger log = LoggerFactory.getLogger(DoubleBinarizer.class);
	private DoubleBinarizer(){}

	public static Double[] binarize(int bitsSize, Collection<String> collection) {
		Double[] result = new Double[bitsSize];
		Arrays.fill(result, 0D);
		for (String value : collection) {
			int intValue = (int)Math.floor(Double.valueOf(value));
			Preconditions.checkArgument(intValue <= bitsSize,
					"data value %s is out of range of binary size %s",
					collection, bitsSize);
			try {result[intValue -1] = 1D;} 
			catch (ArrayIndexOutOfBoundsException e){
				log.error("parsing letter failed {} ",collection);
				throw e;
			}
		}
		return result;
	}

	public static Double[] binarize(int bitSize,Double... data){
		String[] a=Arrays.toString(data).split("[\\[\\]]")[1].split(", ");
		return binarize(bitSize,Arrays.asList(a));
	}
	private boolean isAboveThreshold(Double value) {
		return value > 0.8;
	}

	public static List<Double> sortByValue(Map<Integer, Double> map) {
		List<Map.Entry<Integer, Double>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		List<Double> result = Lists.newArrayList();
		for (Map.Entry<Integer, Double> entry : list ){
			result.add(Double.valueOf(entry.getKey()));
		}
		return result;
	}

	public static Double[] debinarize(int bitsSize,Letter data){
	    return(debinarize(bitsSize, ((Letter<Double> )data).getRawData()));
	}
	public static Double[] debinarize(int bitsSize, double... data){
		return debinarize(bitsSize,ArrayUtils.toObject(data));
	}
	public static Double[] debinarize(int bitsSize, Double... data) {
		List<Double> result = Lists.newArrayList();
		SortedMap<Integer, Double> maxValues = Maps.newTreeMap();
		for (int i = 1; i<=data.length;i++){
			maxValues.put(i,data[i-1]);
		}

		result = sortByValue(maxValues).subList(0, bitsSize);
		return result.toArray(new Double[bitsSize]);
	}

	public static String debinarizeAsString(int bitsSize, Double...data){
		return Arrays.toString(debinarize(bitsSize, data));
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
	   public static double[] normalize(final double[] data,final double threshold){
	        final double[] result = new double[data.length];
	        for (int i=0;i<data.length;i++){
	            result[i] = (data[i] > threshold)?1 : 0;
	        }
	        return result;
	    }
	    
	    public static double[] normalize(final double[] data,final int requiredNumbers){
	        final Double[] result = debinarize(requiredNumbers,data);
	        return ArrayUtils.toPrimitive(result);
	    }
	    public static Letter<Double> normalize(Letter<Double> letter, Double threshold){
	        return new BasicLetter<Double>(ArrayUtils.toObject(normalize(ArrayUtils.toPrimitive(letter.getRawData()),threshold)));
	    }
	    
	    public static Letter<Double> normalize(Letter<Double> letter, int neededLetters){
	        Double[] winners = debinarize(neededLetters,letter);
	        return new BasicLetter<Double>(binarize(letter.getRawData().length, winners));
	    }
}