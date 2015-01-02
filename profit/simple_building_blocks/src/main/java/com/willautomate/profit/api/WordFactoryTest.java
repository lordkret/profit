package com.willautomate.profit.api;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import com.willautomate.profit.impl.*;
public class WordFactoryTest {

	@Test
	public void testToData() {
		Letter<Double> l = new BasicLetter<Double>(ArrayUtils.toArray(1D,2D));
		System.out.println(WordFactory.toData(l));
	}

}
