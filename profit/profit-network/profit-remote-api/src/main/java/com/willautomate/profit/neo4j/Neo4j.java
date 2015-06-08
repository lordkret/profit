package com.willautomate.profit.neo4j;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

}
