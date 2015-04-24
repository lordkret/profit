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
			wordSerice.execute(new LuckyWalker(5500+i,NetworkPattern.Elmann,50,60));
			wordSerice.execute(new LuckyWalker(6500+i,NetworkPattern.Elmann,60,70));
			wordSerice.execute(new LuckyWalker(7500+i,NetworkPattern.Elmann,70,80));
			wordSerice.execute(new LuckyWalker(8500+i,NetworkPattern.Elmann,80,90));
			wordSerice.execute(new LuckyWalker(9500+i,NetworkPattern.Elmann,90,100));
			wordSerice.execute(new LuckyWalker(10000+i,NetworkPattern.Elmann,100,135));
//			wordSerice.execute(new LuckyWalker(i,NetworkPattern.ElmannStep));
			wordSerice.execute(new GrowingWordWalker(100+i,0,NetworkPattern.Elmann,2,30));
			wordSerice.execute(new GrowingWordWalker(200+i,0,NetworkPattern.Elmann,30,45));
			wordSerice.execute(new GrowingWordWalker(300+i,0,NetworkPattern.Elmann,45,60));
			wordSerice.execute(new GrowingWordWalker(400+i,0,NetworkPattern.Elmann,60,80));
			wordSerice.execute(new GrowingWordWalker(500+i,0,NetworkPattern.Elmann,80,135));
//			wordSerice.execute(new GrowingWordWalker(i,3,NetworkPattern.ElmannStep));
//            wordSerice.execute(new GrowingWordWalker(i,3));
//			wordSerice.execute(new GrowingWordWalker(i,1));
        }
		wordSerice.awaitTermination(50, TimeUnit.HOURS);
		
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
