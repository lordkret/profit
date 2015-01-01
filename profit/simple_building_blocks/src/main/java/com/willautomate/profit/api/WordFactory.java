package com.willautomate.profit.api;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;

public class WordFactory {

	public static MLDataSet toDataSet(Word word){
		return null;
		
	}

	public static MLData toData(Letter<?> lastLetter) {
		Object[] mlData =  lastLetter.getRawData();
		new BasicMLData((double[])mlData);
	}

	public static Letter<Double> toLetter(MLData compute) {
		// TODO Auto-generated method stub
		return null;
	}


}
