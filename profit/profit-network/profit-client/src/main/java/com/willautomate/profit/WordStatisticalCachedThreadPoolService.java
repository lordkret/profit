package com.willautomate.profit;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WordStatisticalCachedThreadPoolService extends ThreadPoolExecutor {

	public WordStatisticalCachedThreadPoolService(){
		super(0, Integer.MAX_VALUE,
        60L, TimeUnit.SECONDS,
        new SynchronousQueue<Runnable>());
	}
	public WordStatisticalCachedThreadPoolService(int corePoolSize,
			int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}
	
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		Beemo.finishedPrediction();
		super.afterExecute(r, t);
	}
}
