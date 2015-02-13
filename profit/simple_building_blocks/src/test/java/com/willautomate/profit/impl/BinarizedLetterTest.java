package com.willautomate.profit.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import com.willautomate.profit.api.Letter;

public class BinarizedLetterTest {

	@Test
	public void test() {
		Double[] a = {1D,2D,3D};

		double[] b = {1,2,3};
		Double[] bo = ArrayUtils.toObject(b);
		double[] c = {4,2,4};
		Double[] co = ArrayUtils.toObject(c);
		Letter<Double> lA = new BinarizedLetter<Double>(new BasicLetter<Double>(a));
		Letter<Double> lB = new BinarizedLetter<Double>(new BasicLetter<Double>(bo));
		Letter<Double> lC = new BinarizedLetter<Double>(new BasicLetter<Double>(co));
		assertTrue(lA.equals(lB));
		assertThat(lA, equalTo(lB));
		assertFalse(lA.equals(lC));
		assertThat(lA, not(equalTo(lC)));
	}

}
