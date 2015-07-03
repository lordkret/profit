package com.willautomate.profit.neo4j;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Neo4j {
	private static final String SERVER_ROOT_URI = "http://profit.willautomate.com:7474/db/data/";
	private static final Logger log = LoggerFactory.getLogger(Neo4j.class);
	public static boolean checkDatabaseIsRunning()
	{
		WebResource resource = Client.create()
				.resource( SERVER_ROOT_URI );
		ClientResponse response = resource.get( ClientResponse.class );

		log.debug( String.format( "GET on [%s], status code [%d]",
				SERVER_ROOT_URI, response.getStatus() ) );
		boolean running = response.getStatus() == 200;
		response.close();
		return running;
	}
	private static String sendQuery(String query){
		final String txUri = SERVER_ROOT_URI + "transaction/commit";
		WebResource resource = Client.create().resource( txUri );

		log.info("Posting {}",query);
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
	public static String sendTransactionalCypherQuery(String query) {

		String payload = "{\"statements\" : [ {\"statement\" : \"" +query.replaceAll("\"","\\\\\"") + "\"} ]}";

		return sendQuery(payload);
	}
	
	//This method was for 1 use only
	private static void addLetterId(){
		String neo4jAnswer = Neo4j.sendTransactionalCypherQuery("match (l:Letter) return Id(l) order by Id(l)");
		log.info(neo4jAnswer);
		
			JsonElement el = new JsonParser().parse(neo4jAnswer);
			JsonArray data = el.getAsJsonObject().
					get("results")
					.getAsJsonArray().get(0)
					.getAsJsonObject().get("data")
					.getAsJsonArray();
			for (int i=0;i<data.size();i++){
					String result = data.get(i)
					.getAsJsonObject().get("row")
					.getAsJsonArray().get(0)
					.getAsString();
					sendTransactionalCypherQuery(String.format("match (l:Letter) where Id(l) = %s set l.letterId = %s",result,i+1));
			}
	
	}

}
