package com.willautomate.profit.impl;

import java.util.Iterator;
import java.util.List;

import org.encog.ml.MLMethod;
import org.encog.ml.TrainingImplementationType;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.TrainingContinuation;

import com.google.common.collect.Lists;

public class SomeOtherTraining  implements Train{


	private final MLDataSet dataSet;
	private final BasicNetwork network;

	private double error;

	private int iteration;
	private int iterationsToDo;

	public SomeOtherTraining(final MLDataSet dataSet, final BasicNetwork network) {
		this.dataSet = dataSet;
		this.network = network;
	}

	@Override
	public TrainingImplementationType getImplementationType() {
		return TrainingImplementationType.Iterative;
	}
	@Override
	public boolean isTrainingDone() {
		return getError() == 0;	}

	@Override
	public MLDataSet getTraining() {
		return dataSet;
	}
	@Override
	public void iteration() {
		Iterator<MLDataPair> pairs = dataSet.iterator();
		MLDataPair pair = null;

		while (((pair = pairs.next())!= null) ){
			double[] input = pair.getInputArray();
			double[] expected = pair.getIdealArray();
			double[] computed = new double[expected.length];
			network.compute(input, computed);
			
			List<Integer> distance = DoubleLetterDistance.calculate(expected,computed);
			setError(getError()+distance.size());
			}

	}

	@Override
	public double getError() {
		return error;
	}

	@Override
	public void finishTraining() {
		this.iterationsToDo = 0;
	}

	@Override
	public void iteration(int count) {
		this.iterationsToDo = count;
	}

	@Override
	public int getIteration() {
		return iteration;
	}

	@Override
	public boolean canContinue() {
		return true;
	}

	@Override
	public TrainingContinuation pause() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resume(TrainingContinuation state) {
		// TODO Auto-generated method stub

	}

	private final List<Strategy> strategies = Lists.newArrayList();
	@Override
	public void addStrategy(Strategy strategy) {
		strategies.add(strategy);

	}

	@Override
	public MLMethod getMethod() {
		return network;
	}

	@Override
	public List<Strategy> getStrategies() {
		return strategies;
	}

	@Override
	public void setError(double error) {
		this.error = error;
	}

	@Override
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

}
