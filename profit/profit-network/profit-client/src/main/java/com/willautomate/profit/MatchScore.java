package com.willautomate.profit;

import java.util.Iterator;
import java.util.List;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.BasicNetwork;

import com.willautomate.profit.impl.DoubleLetterDistance;

public class MatchScore implements CalculateScore{

	private final MLDataSet set;
	public MatchScore(MLDataSet set) {
		this.set = set;
	}
	@Override
	public double calculateScore(MLMethod method) {
		double result = 0;
		final Iterator<MLDataPair> pairs = set.iterator();
    	MLDataPair pair = null;
    	


		while (((pair = pairs.next())!= null) ){
			double[] input = pair.getInputArray();
			double[] expected = pair.getIdealArray();
			double[] computed = new double[expected.length];
			((BasicNetwork) method).compute(input, computed);
			
			List<Integer> distance = DoubleLetterDistance.calculate(expected,computed);
			result += distance.size();
			}
		
		return result;
	}

	@Override
	public boolean shouldMinimize() {
		return true;
	}

	@Override
	public boolean requireSingleThreaded() {
		return true;
	}

}
