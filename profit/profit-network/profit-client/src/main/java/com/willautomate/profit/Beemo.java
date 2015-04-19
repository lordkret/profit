package com.willautomate.profit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.encog.Encog;
import org.encog.engine.opencl.EncogCLDevice;
import org.neo4j.examples.server.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Beemo {

	static ScheduledExecutorService clientRemoteService = Executors.newSingleThreadScheduledExecutor();
	private static AtomicBoolean unregistered = new AtomicBoolean(true);
	private static AtomicInteger letters = new AtomicInteger(0);
	private static AtomicInteger predictions = new AtomicInteger(0);
	public static ReentrantReadWriteLock.WriteLock shutdownLock = new ReentrantReadWriteLock().writeLock();
	
	private Beemo() {}
	
private static AtomicInteger dev = new AtomicInteger(0);
	public static synchronized EncogCLDevice getDevice(){
		List<EncogCLDevice> devices = Encog.getInstance().getCL().getDevices();
		if (dev.get() >= devices.size()){
			dev.set(0);
		}
		return devices.get(dev.getAndIncrement());
	}
	
	private static String client= null;
	private static String getClientName(){
		if (client == null){
		try {
			client =  InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			client =  "maligna";
		}
		} 
		return client;

	}
	private static Runnable checkForKill = new Runnable() {
 
		@Override
		public void run() {
			String query = String.format("match (client:Client{name:'%s'}) return client.status, client.threads", getClientName());
			String update = String.format("match (client:Client{name:'%s'}) set client.letterRatio='%s/s', client.lastUpdate='%s'", getClientName(),letters.getAndSet(0)/10,new Date());
			Connector.sendTransactionalCypherQuery(update);
			String result = Connector.sendTransactionalCypherQuery(query);
			if (result.contains("kill")){
					clientRemoteService.submit(unregister);
					clientRemoteService.shutdown();
					unregistered.set(true);
			} else {
				
//				Client.wordSerice.setMaximumPoolSize(maximumPoolSize);
			}
		}
	};
	private static final Logger log = LoggerFactory.getLogger(Beemo.class);
	private static Callable register = new Callable() {
		
		@Override
		public Object call() throws Exception {
			shutdownLock.lock();
			String getClient = String.format("match (client:Client{name:'%s'}) return client",getClientName() );
			String clientOutput = Connector.sendTransactionalCypherQuery(getClient);
			String query = "";
			if (clientOutput.contains(getClientName())){
				query = String.format("match (client:Client{name:'%s'}) set client.status='alive'",getClientName() );;
			} else {
			query = String.format("create (client:Client{name:'%s', status:'alive'}) ",getClientName() ); 
			}
			Connector.sendTransactionalCypherQuery(query);
			log.info("registered " + clientOutput);
			unregistered.set(false);
			return null;
		}
	};
	private static Callable unregister = new Callable() {
		
		@Override
		public Object call() {
			System.out.println("unlocking");
			Connector.sendTransactionalCypherQuery(String.format("match (client:Client{name:'%s'}) set client.status='dead',client.letterRatio='0/s',client.predictionRatio='0/s'",getClientName()));
			shutdownLock.unlock();		
			System.out.println("unlocked");
			return null;
		}
	};
	public static void register() throws InterruptedException, UnknownHostException, ExecutionException {
		
		log.info("registering client " );
		clientRemoteService.submit(register).get();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			
			@Override
			public void run() {try {
				unregister();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				log.error("Problem unregistering",e);
			}
			}
		}));
		
		clientRemoteService.scheduleAtFixedRate(checkForKill, 10, 10, TimeUnit.SECONDS);
		
	}	
	
	public static void unregister() throws InterruptedException{
		if (! unregistered.get()){
		clientRemoteService.submit(unregister);
		clientRemoteService.shutdown();
		unregistered.set(true);
		}
		if (! clientRemoteService.isTerminated()){
			clientRemoteService.awaitTermination(10, TimeUnit.MINUTES);
		}
		Connector.disconnect();
	}
	public static synchronized void finishedLetter(){
		letters.incrementAndGet();
	}
	
	public static synchronized void finishedPrediction(){
		int preds = predictions.incrementAndGet();
		String update = String.format("match (client:Client{name:'%s'}) set client.letterRatio='%s/s',client.predictionRatio='%s', client.lastPrediction='%s'", getClientName(),letters.getAndSet(0)/10,preds,new Date());
		Connector.sendTransactionalCypherQuery(update);
	
	}
	
	
}
