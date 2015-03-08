package com.willautomate.profit.impl;



import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import static org.hamcrest.Matcher.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
public class BasicLetterTest {

	@Test
	public void test() {
		Double[] a = {1D,2D,3D};

		double[] b = {1,2,3};
		Double[] bo = ArrayUtils.toObject(b);
		double[] c = {4,2,4};
		Double[] co = ArrayUtils.toObject(c);
		System.out.println(Arrays.deepToString(co) +  " " + Arrays.equals(b, c)) ;
		assertTrue(new BasicLetter<Double>(a).equals(new BasicLetter<Double>(bo) ));
		assertThat(new BasicLetter<Double>(a), equalTo(new BasicLetter<Double>(bo)));
		assertFalse(new BasicLetter<Double>(a).equals(new BasicLetter<Double>(co) ));
		assertThat(new BasicLetter<Double>(a), not(equalTo(new BasicLetter<Double>(co))));
	}

}
