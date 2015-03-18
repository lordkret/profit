package com.willautomate.profit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Client {

	public static final ThreadPoolExecutor letterService = new LetterStatisticalCachedThreadPoolService();
	public static final ThreadPoolExecutor wordSerice = new WordStatisticalCachedThreadPoolService();
	
	public static void main(String[] objs) throws Exception{
		System.out.println("Starting");
		Beemo.register();
		for (int i = 0; i < 50; i++) {
			wordSerice.execute(new LuckyWalker(i));
			wordSerice.execute(new GrowingWordWalker(i,2));
//            wordSerice.execute(new GrowingWordWalker(i,3));
			wordSerice.execute(new GrowingWordWalker(i,1));
        }
		
		Beemo.shutdownLock.lock();
		
		letterService.shutdownNow();
		
		wordSerice.shutdownNow();
		
		System.out.println("Stopping");
		Beemo.shutdownLock.unlock();
	}
}
