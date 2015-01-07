package com.willautomate.profit.api;

import java.util.Arrays;
import java.util.Objects;

import com.willautomate.profit.impl.BasicLetter;
import org.apache.commons.lang3.ArrayUtils;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;

public class WordFactory {

	public static MLDataSet toDataSet(Word word){
		Letter<Double>[] letters = word.getLetters();
		double[][] input = new double[letters.length-1][letters[0].getRawData().length];
		double[][] ideal = new double[letters.length-1][letters[0].getRawData().length];		
		
		for (int i = 1; i<= letters.length-1;i++){
			input[i-1] = ArrayUtils.toPrimitive(letters[i-1].getRawData());
			ideal[i-1] = ArrayUtils.toPrimitive(letters[i].getRawData());
			
		}
		System.out.println(Arrays.deepToString(input) + Arrays.deepToString(ideal));
		MLDataSet data = new BasicMLDataSet(input, ideal);
		return data;
		
	}

	
	public static MLData toData(Letter<?> lastLetter) {
		Object[] mlData =  lastLetter.getRawData();
		double[] d = ArrayUtils.toPrimitive(Arrays.copyOf(mlData,mlData.length,Double[].class));
		return new BasicMLData(d);
	}

	public static Letter<Double> toLetter(MLData compute) {
		return new BasicLetter<Double>(ArrayUtils.toObject(compute.getData()));
	}


}
