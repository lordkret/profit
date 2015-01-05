package com.willautomate.profit.api;

import static org.junit.Assert.*;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.BasicWord;

public class WordFactoryTest {

	@Test
	public void testToDataSet() {
		Letter<Double> l1 = new BasicLetter<Double>(ArrayUtils.toArray(1D,2D));
		Letter<Double> l2 = new BasicLetter<Double>(ArrayUtils.toArray(3D,4D));
		Word w = new BasicWord(l1,l2);
		System.out.println( WordFactory.toDataSet(w).get(0));
		
	}
	
	@Test
	public void testToData() {
		Letter<Double> l = new BasicLetter<Double>(ArrayUtils.toArray(1D,2D));
		System.out.println(WordFactory.toData(l));
	}

}
