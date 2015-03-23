package com.willautomate.profit.impl;



import org.encog.ml.data.MLDataSet;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrainingReEngineering  extends ResilientPropagation{

	private final ContainsFlat network;
	private final MLDataSet data;
	
	private final Logger log = LoggerFactory.getLogger(TrainingReEngineering.class);
	public TrainingReEngineering(ContainsFlat network, MLDataSet training) {
		super(network, training);
		this.network = network;
		data = training;
	}

	/**
	 * Perform one training iteration.
	 */
	public void iteration() {
		iteration(1);
	}
	/**
	 * Perform one training iteration.
	 */
	public void iteration(int i) {
		log.info("Executing iteration {}",i);
		super.iteration(i);
	}
}
