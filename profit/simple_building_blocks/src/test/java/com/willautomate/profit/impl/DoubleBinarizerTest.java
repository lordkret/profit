package com.willautomate.profit.impl;


import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
public class DoubleBinarizerTest {

	@Test
	public void test() {
		DoubleBinarizer bourbon = new DoubleBinarizer();
		
		Double[] lame = bourbon.binarize(5, 2,3,4);
		System.out.println(Arrays.deepToString(lame));
		Double[] expected = {0D,1D,1D,1D,0D};
		assertThat(lame,equalTo(expected));
		
		lame = bourbon.debinarize(3, 0D,1D,1D,1D,0D);
		Double[] expectedAgain = {2D,3D,4D};
		assertThat(lame, equalTo(expectedAgain));
	}

	@Test
	public void binarizeUsingCollection(){
		DoubleBinarizer binarizer = new DoubleBinarizer();
		Collection<String> collection = new ArrayList<String>();
		collection.add("2");
		collection.add("3");
		collection.add("4");
		Double[] expected = {0D,1D,1D,1D,0D};
		assertThat(binarizer.binarize(5, collection), equalTo(expected));
	}

}
