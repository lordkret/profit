package com.willautomate.profit;

import static org.junit.Assert.*;

import org.junit.Test;

public class TimeWalkerTest {

	@Test
	public void test() throws Exception {
		new Thread(new TimeWalker()).start();
		Thread.sleep(60000);
	}

}
