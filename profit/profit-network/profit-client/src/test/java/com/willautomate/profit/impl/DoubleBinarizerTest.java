package com.willautomate.profit.impl;


import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
public class DoubleBinarizerTest {
	
	@Test
	public void test() {
		
		
		Double[] lame = DoubleBinarizer.binarize(5, 2,3,4);
		System.out.println(Arrays.deepToString(lame));
		Double[] expected = {0D,1D,1D,1D,0D};
		assertThat(lame,equalTo(expected));
		Double[] td = {0D,1D,1D,1D,0D};
		lame = DoubleBinarizer.debinarize(3, td);
		Double[] expectedAgain = {2D,3D,4D};
		assertThat(lame, equalTo(expectedAgain));
	}

	@Test
	public void binarizeUsingCollection(){

		Collection<String> collection = new ArrayList<String>();
		collection.add("1");
		collection.add("2");
		collection.add("5");
		Double[] expected = {1D,1D,0D,0D,1D};
		assertThat(DoubleBinarizer.binarize(5, collection), equalTo(expected));
	}

	
	@Test
	public void checkDebinarization(){
		Collection<String> collection = new ArrayList<String>();
		collection.add("1");
		collection.add("2");
		collection.add("5");
		int[] d = {1,2,5};
		int[] f = {1,2,3};
		assertThat(DoubleBinarizer.debinarize(3, DoubleBinarizer.binarize(5, collection)),equalTo(DoubleBinarizer.debinarize(3, DoubleBinarizer.binarize(5, d))));
		assertThat(DoubleBinarizer.debinarize(3, DoubleBinarizer.binarize(5, collection)),not(equalTo(DoubleBinarizer.debinarize(3, DoubleBinarizer.binarize(5, f)))));
		
	}
	
}
