package com.willautomate.profit.impl;

import org.junit.Test;

import com.willautomate.profit.api.Letter;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class DoubleLetterDistanceTest {

	@Test
	public void test() {
		Letter<Double> a = new BasicLetter<Double>(0D,0D,1D,2D);
		Letter<Double> b = new BasicLetter<Double>(0D,1D,2D,0D);
		assertThat(DoubleLetterDistance.calculate(a, b, 2),equalTo(1D));
	}

}
