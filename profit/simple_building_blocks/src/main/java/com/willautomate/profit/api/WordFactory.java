package com.willautomate.profit.api;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;

public class WordFactory {

	public static MLDataSet toDataSet(Word word){
		return null;
		
	}

	public static MLData toData(Letter<?> lastLetter) {
		Object[] mlData =  lastLetter.getRawData();
		double[] d = ArrayUtils.toPrimitive(Arrays.copyOf(mlData,mlData.length,Double[].class));
		return new BasicMLData(d);
	}

	public static Letter<Double> toLetter(MLData compute) {
		// TODO Auto-generated method stub
		return null;
	}


}
