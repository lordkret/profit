package com.willautomate.profit;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.willautomate.profit.api.WalkerConfiguration.NetworkPattern;

public class Client {

	public static final ThreadPoolExecutor letterService = new LetterStatisticalCachedThreadPoolService();
	public static final ThreadPoolExecutor wordSerice = new WordStatisticalCachedThreadPoolService();
	
	public static void main(String[] objs) throws Exception{
		System.out.println("Starting");
		Beemo.register();
		for (int i = 0; i < 1000; i++) {
			wordSerice.execute(new LuckyWalker(100+i,NetworkPattern.Elmann));
//			wordSerice.execute(new LuckyWalker(i,NetworkPattern.ElmannStep));
			wordSerice.execute(new GrowingWordWalker(100+i,2,NetworkPattern.Elmann));
			wordSerice.execute(new GrowingWordWalker(i,1,NetworkPattern.ElmannStep));
//			wordSerice.execute(new GrowingWordWalker(i,3,NetworkPattern.ElmannStep));
//            wordSerice.execute(new GrowingWordWalker(i,3));
//			wordSerice.execute(new GrowingWordWalker(i,1));
        }
		wordSerice.awaitTermination(24, TimeUnit.HOURS);
		
		System.out.println("Ready to stop");
		Beemo.unregister();
		
		Beemo.shutdownLock.lock();

		
		letterService.shutdownNow();
		
		wordSerice.shutdownNow();
		
		System.out.println("Stopping");
		Beemo.shutdownLock.unlock();
		System.exit(0);
	}
}
