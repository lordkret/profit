package com.willautomate.profit;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class WeightsDispatcherTest {

	@Test
	public void testWeights() {
		for (int i=0;i<22;i++){
		System.out.println(Arrays.toString(WeightsDispatcher.weights(2)));
		}
		
	}

}
