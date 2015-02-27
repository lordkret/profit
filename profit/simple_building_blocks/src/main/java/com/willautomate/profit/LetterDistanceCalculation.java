package com.willautomate.profit;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;

import com.willautomate.profit.api.Word;

public class LetterDistanceCalculation implements CalculateScore{

	private final Word w;
	
	public LetterDistanceCalculation(Word w) {
		this.w=w;
	}
	@Override
	public double calculateScore(MLMethod method) {
		double overallDistance = 0; 
		
		return overallDistance;
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
