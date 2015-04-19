package com.willautomate.profit;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.encog.Encog;

import com.willautomate.profit.api.WalkerConfiguration.NetworkPattern;

public class Client {

	public static final ThreadPoolExecutor letterService = new LetterStatisticalCachedThreadPoolService();
	public static final ThreadPoolExecutor wordSerice = new WordStatisticalCachedThreadPoolService();

	public static void main(String[] objs) throws Exception{
		System.out.println("Starting");
		Encog.getInstance().initCL();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				Encog.getInstance().shutdown();		
			}
		}));

		Beemo.register();
		for (int i = 0; i <1000; i++) {
			wordSerice.execute(new LuckyWalker(4500+i,NetworkPattern.Elmann,2,50));
			wordSerice.execute(new LuckyWalker(5500+i,NetworkPattern.Elmann,50,60));
			wordSerice.execute(new LuckyWalker(6500+i,NetworkPattern.Elmann,60,70));
			wordSerice.execute(new LuckyWalker(7500+i,NetworkPattern.Elmann,70,80));
			wordSerice.execute(new LuckyWalker(8500+i,NetworkPattern.Elmann,80,90));
			wordSerice.execute(new LuckyWalker(9500+i,NetworkPattern.Elmann,90,100));
			wordSerice.execute(new LuckyWalker(10000+i,NetworkPattern.Elmann,2,127));
			//			wordSerice.execute(new LuckyWalker(i,NetworkPattern.ElmannStep));
			wordSerice.execute(new GrowingWordWalker(100+i,2,NetworkPattern.Elmann));
			wordSerice.execute(new GrowingWordWalker(i,1,NetworkPattern.ElmannStep));
			wordSerice.execute(new GrowingWordWalker(1000+i,0,NetworkPattern.ElmannStep));
			//			wordSerice.execute(new GrowingWordWalker(i,3,NetworkPattern.ElmannStep));
			//            wordSerice.execute(new GrowingWordWalker(i,3));
			//			wordSerice.execute(new GrowingWordWalker(i,1));
		}
		wordSerice.awaitTermination(40, TimeUnit.HOURS);

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
