package com.willautomate.profit;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WordStatisticalCachedThreadPoolService extends ThreadPoolExecutor {

	private static final int getMaxPoolSize(){
		int result = Runtime.getRuntime().availableProcessors();
		String maxPool = System.getProperty("threads");
		if (maxPool != null ){
			result = Integer.decode(maxPool);
		}
		return result;
	}
	public WordStatisticalCachedThreadPoolService(){
		this(getMaxPoolSize(), getMaxPoolSize(),
        60L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>());
	}
	public WordStatisticalCachedThreadPoolService(int corePoolSize,
			int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		System.out.println(String.format("created pool executor with %s core size and %s max size",corePoolSize,maximumPoolSize));
	}
	
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		Beemo.finishedPrediction();
		super.afterExecute(r, t);
	}
}
