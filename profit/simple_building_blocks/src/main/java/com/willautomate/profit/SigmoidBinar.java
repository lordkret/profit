package com.willautomate.profit;

import org.encog.engine.network.activation.ActivationBiPolar;

import org.encog.engine.network.activation.ActivationFunction;

import org.encog.ml.factory.MLActivationFactory;
import org.encog.util.obj.ActivationUtil;

public class SigmoidBinar implements ActivationFunction{

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -7166136514935838114L;

	/**
	 * The parameters.
	 */
	private final double[] params;

	/**
	 * Construct the bipolar activation function.
	 */
	public SigmoidBinar() {
		this.params = new double[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public final void activationFunction(final double[] x, final int start,
			final int size) {

		for (int i = start; i < start + size; i++) {
			if (x[i] > 0) {
				x[i] = 1;
			} else {
				x[i] = 0;
			}
		}
	}

	/**
	 * @return The object cloned.
	 */
	@Override
	public final ActivationFunction clone() {
		return new SigmoidBinar();
	}

	/**
	 * {@inheritDoc}
	 */
	public final double derivativeFunction(double b, double a) {
		return 1;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String[] getParamNames() {
		final String[] result = {};
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	
	public final double[] getParams() {
		return this.params;
	}

	/**
	 * @return Return true, bipolar has a 1 for derivative.
	 */
	
	public final boolean hasDerivative() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	
	public final void setParam(final int index, final double value) {
		this.params[index] = value;
	}

	/**
	 * {@inheritDoc}
	 */
	
	public String getFactoryCode() {
		return ActivationUtil.generateActivationFactory(MLActivationFactory.AF_BIPOLAR, this);
	}

}
