/**
 * Licensed to Neo Technology under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.neo4j.examples.server;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Connector
{
	private static final String SERVER_ROOT_URI = "http://profit.willautomate.com:7474/db/data/";

	public static void main( String[] args ) throws Exception
	{
		checkDatabaseIsRunning();
//		sendUnsentQueries();

		//        createNumbers();
		//        [14.0, 23.0, 24.0, 30.0, 49.0]
		createLetter(35,37,31,26,30,11,8);
		//        createPrediction(14, 23, 24, 30, 49, 0, 0, 30, 2);
	}

	public static void createPrediction(int m1,int m2,int m3, int m4, int m5, int l1, int l2, int wordSize, int distance, String pattern, double weightValue,boolean isSmart){
		StringBuilder sb = new StringBuilder("match (for:Letter) ");
		if (m1 != 0){
			sb.append(",(m1:Number),"
					+ "(m2:Number),"
					+ "(m3:Number),"
					+ "(m4:Number),"
					+ "(m5:Number)");
		}
		if (l1 != 0){
			sb.append(",(l1:Number),"
					+ "(l2:Number)");
		}
		sb.append(" where ");
		if (m1 != 0){
			sb.append(String.format("m1.value=%s and m2.value=%s and m3.value=%s and m4.value=%s and m5.value=%s and ",m1,m2,m3,m4,m5));
		}
		if (l1 != 0){
			sb.append(String.format("l1.value=%s and l2.value=%s and " , l1,l2));
		}
		sb.append(" has(for.LATEST) ");
		sb.append(String.format("create (n:Prediction:Letter {wordsize:%s, distance:%s, pattern:'%s', weight:%s, smart:%s}) "
				+ "create (n)-[:FOR]->(for) ",wordSize,distance,pattern,weightValue,isSmart));
		if (m1 != 0){
			sb.append("create (n)-[:MAIN]->(m1) "
					+ "create (n)-[:MAIN]->(m2) "
					+ "create (n)-[:MAIN]->(m3) "
					+ "create (n)-[:MAIN]->(m4) "
					+ "create (n)-[:MAIN]->(m5) ");
		}
		if (l1 != 0){
			sb.append("create (n)-[:LUCKY]->(l1) "
					+ "create (n)-[:LUCKY]->(l2) ");
		}
		sb.append(" return n");
		sendTransactionalCypherQuery(sb.toString());

	}
	private static void createLetter(int m1,int m2,int m3, int m4, int m5, int l1, int l2){
		sendTransactionalCypherQuery("match (l:Letter {LATEST:true}) remove l.LATEST return l");
		sendTransactionalCypherQuery( String.format("match "
				+ "(m1:Number),"
				+ "(m2:Number),"
				+ "(m3:Number),"
				+ "(m4:Number),"
				+ "(m5:Number),"
				+ "(l1:Number),"
				+ "(l2:Number) "
				+ "where m1.value=%s and m2.value=%s and m3.value=%s and m4.value=%s and m5.value=%s"
				+ " and l1.value=%s and l2.value=%s"
				+ " create (n:Letter {LATEST:true}) "
				+ " create (n)-[:MAIN]->(m1) "
				+ "create (n)-[:MAIN]->(m2) "
				+ "create (n)-[:MAIN]->(m3) "
				+ "create (n)-[:MAIN]->(m4) "
				+ "create (n)-[:MAIN]->(m5) "
				+ "create (n)-[:LUCKY]->(l1) "
				+ "create (n)-[:LUCKY]->(l2) "
				+ "return Id(n)",m1,m2,m3,m4,m5,l1,l2));

	}
	private static void createNumbers(){
		for (int i=1;i<=50;i++){
			sendTransactionalCypherQuery(String.format("create (n:Number {value:%s})", i));
		}
	}
	public static String sendTransactionalCypherQuery(String query) {

		String payload = "{\"statements\" : [ {\"statement\" : \"" +query + "\"} ]}";

		return sendIfPossibleOrPostpone(payload);
	}


	private static void checkDatabaseIsRunning()
	{
		WebResource resource = Client.create()
				.resource( SERVER_ROOT_URI );
		ClientResponse response = resource.get( ClientResponse.class );

		log.debug( String.format( "GET on [%s], status code [%d]",
				SERVER_ROOT_URI, response.getStatus() ) );
		response.close();
	}

	private static BlockingQueue<String> queries = new LinkedBlockingQueue<String>();
	private static Logger log = LoggerFactory.getLogger(Connector.class);

	private static String sendIfPossibleOrPostpone(String query){
		try {
			return sendQuery(query);
		} catch (Throwable issue){
			log.warn("Can't send now due to {}. Will retry later",issue);
			queries.add(query);
			return "";
		}
	}

	private static String sendQuery(String query){
		final String txUri = SERVER_ROOT_URI + "transaction/commit";
		WebResource resource = Client.create().resource( txUri );


		ClientResponse response = resource
				.accept( MediaType.APPLICATION_JSON )
				.type( MediaType.APPLICATION_JSON )
				.entity( query )
				.post( ClientResponse.class );
		String result = response.getEntity( String.class ) ;
		log.info( String.format(
				"POST [%s] to [%s], status code [%d], returned data: "
						+ System.getProperty( "line.separator" ) + "%s",
						query, txUri, response.getStatus(),
						result ) );
		if (response.getStatus()!=200)
			log.warn(String.format(
					"POST [%s] to [%s], status code [%d], returned data: "
							+ System.getProperty( "line.separator" ) + "%s",
							query, txUri, response.getStatus(),
							result ));
		response.close();
		return result;
	}

	private static ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	static {
		service.scheduleAtFixedRate(new Runnable() {		
			@Override
			public void run() {sendQuery();
			}
		}, 10, 10, TimeUnit.SECONDS);
	}
	private static void sendQuery(){
		if (queries.size() > 0){
			String query = queries.peek();
			try {
				sendQuery(query);
				queries.poll();	
				log.warn("Query {} sent",query);

			} catch (Throwable issue){
				log.warn("A bit of issue. Queue size {}. Will try later with query {}", queries.size(),query);
			}
		}
	}

	public static void disconnect(){
		try {
			service.awaitTermination(15, TimeUnit.SECONDS);
			for (String q : queries){
				Files.write(Paths.get("unsentQueries"), String.format("%s\n", q).getBytes(),StandardOpenOption.APPEND);
			}
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			log.error("disconnecting issue",e);
		}
	}
	public static void sendUnsentQueries() throws IOException{
		List<String> qs = Files.readAllLines(Paths.get("/home/erafkos/profit/profit/profit-network/profit-client/unQ"), Charset.defaultCharset());
		for (String q : qs){
			sendQuery(q);
		}
		disconnect();
	}
}
