package com.willautomate.profit.daemon;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.willautomate.profit.neo4j.Neo4j;

public class DataVomiter {

	public static String getLastDate(){
		String result = Neo4j.sendTransactionalCypherQuery("match (l:Letter {LATEST:true}) return l.date");
		return getFirstRestult(result);
	}
	
	private static final Logger log = LoggerFactory.getLogger(DataVomiter.class);
	public static String getFirstRestult(String neo4jAnser){
		System.out.println(neo4jAnser);
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
	public static String getNextData(String date) throws ParseException{
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy"); 
		Date start = df.parse(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(start);
		if (Calendar.TUESDAY == cal.get(Calendar.DAY_OF_WEEK)){
			cal.add(Calendar.DATE, 3);
		} else cal.add(Calendar.DATE, 4);
		return  df.format(cal.getTime());
		
	}
	public static void vomitData() throws ParseException{
		String start = DataSlurper.BEGGINING_OF_TIME;
		try {
			start = getNextData(getLastDate());
		} catch (IndexOutOfBoundsException e){}
		for (String uri : DataSlurper.getUris(start)){
			log.info("Slurping for {}",uri);
			Neo4j.sendTransactionalCypherQuery(DataSlurper.createLetter(uri));	
		}
	}
	
}
