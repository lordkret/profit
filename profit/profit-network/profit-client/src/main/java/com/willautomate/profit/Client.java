package com.willautomate.profit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.willautomate.profit.api.WalkerConfiguration.NetworkPattern;

public class Client {

	public static final ThreadPoolExecutor letterService = new LetterStatisticalCachedThreadPoolService();
	public static final ThreadPoolExecutor wordSerice = new WordStatisticalCachedThreadPoolService();
	
	public static void main(String[] objs) throws Exception{
		System.out.println("Starting");
		Beemo.register();
		for (int i = 0; i < 1; i++) {
			wordSerice.execute(new LuckyWalker(i,NetworkPattern.Elmann));
			wordSerice.execute(new LuckyWalker(i,NetworkPattern.ElmannStep));
			//wordSerice.execute(new GrowingWordWalker(i,2));
//            wordSerice.execute(new GrowingWordWalker(i,3));
//			wordSerice.execute(new GrowingWordWalker(i,1));
        }
		
//		Beemo.shutdownLock.lock();
		Beemo.unregister();
		letterService.shutdownNow();
		
		wordSerice.shutdownNow();
		
		System.out.println("Stopping");
//		Beemo.shutdownLock.unlock();
	}
}
