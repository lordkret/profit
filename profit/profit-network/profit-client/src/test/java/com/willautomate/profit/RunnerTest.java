package com.willautomate.profit;

import static org.junit.Assert.*;

import org.junit.Test;

public class RunnerTest {

	@Test
	public void test() throws InterruptedException {
		new ElmanWordDetectorRunner().threeStream();
	}

}
