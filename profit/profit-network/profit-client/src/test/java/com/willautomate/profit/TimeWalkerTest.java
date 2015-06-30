package com.willautomate.profit;

import static org.junit.Assert.*;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TimeWalkerTest {
	public static final ThreadPoolExecutor wordSerice = new WordStatisticalCachedThreadPoolService();
	
	@Test
	public void test() throws Exception {
		Beemo.register();
		for (int i = 0; i < 10; i++) {
			wordSerice.execute(new TimeWalker());
			
		}
		wordSerice.awaitTermination(15, TimeUnit.HOURS);
		
		Beemo.unregister();
		
		Beemo.shutdownLock.lock();

		wordSerice.shutdownNow();
		
		Beemo.shutdownLock.unlock();
		System.exit(0);
	}

}
