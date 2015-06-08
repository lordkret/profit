package com.willautomate.profit.daemon;

import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.willautomate.profit.neo4j.Neo4j;

public class DataVomiter {

	public static String getLastDate(){
		String result = Neo4j.sendTransactionalCypherQuery("match (l:Letter {LATEST:true}) return l.date");
		return getFirstRestult(result);
	}
	
	private static final Logger log = LoggerFactory.getLogger(DataVomiter.class);
	private static String getFirstRestult(String neo4jAnser){
		JsonElement el = new JsonParser().parse(neo4jAnser);
		return el.getAsJsonObject().
				get("results")
				.getAsJsonArray().get(0)
				.getAsJsonObject().get("data")
				.getAsJsonArray().get(0)
				.getAsJsonObject().get("row")
				.getAsJsonArray().get(0)
				.getAsString();
	}
	public static void vomitData() throws ParseException{
		String start = getLastDate();
		for (String uri : DataSlurper.getUris(start)){
			log.info("Slurping for {}",uri);
			Neo4j.sendTransactionalCypherQuery(DataSlurper.createLetter(uri));	
		}
	}
	
}
