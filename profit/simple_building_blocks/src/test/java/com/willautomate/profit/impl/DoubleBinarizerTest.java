package com.willautomate.profit.impl;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
public class DoubleBinarizerTest {

	@Test
	public void test() {
		DoubleBinarizer bourbon = new DoubleBinarizer();
		
		Double[] lame = bourbon.binarize(5, 2,3,4);
		Double[] expected = {0D,1D,1D,1D,0D};
		assertThat(lame,equalTo(expected));
		
		lame = bourbon.debinarize(3, 0D,1D,1D,1D,0D);
		Double[] expectedAgain = {2D,3D,4D};
		assertThat(lame, equalTo(expectedAgain));
	}

}
