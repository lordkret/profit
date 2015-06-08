package com.willautomate.profit.daemon;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.stringtemplate.v4.ST;
public class DataSlurper {

	public static final String BEGGINING_OF_TIME = "10-05-2011";
	
	public void preSlurp(){
		
	}
	
	public void microSlurp(){

	}
	
	public static List <String> getUris() throws ParseException{
		List<Date> results = Lists.newArrayList();
		Calendar start = Calendar.getInstance();
		final DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); 
		start.setTime(formatter.parse(BEGGINING_OF_TIME));
		Calendar end = Calendar.getInstance();
		end.setTime(new Date());
		
		for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 7), date = start.getTime()) {
			results.add(date);
		}
		start.setTime(formatter.parse(BEGGINING_OF_TIME));
		start.add(Calendar.DATE, 3);
		for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 7), date = start.getTime()) {
			results.add(date);
		}		
		Collections.sort(results);
		return Lists.transform(results, new Function<Date, String>() {

			@Override
			public String apply(Date arg0) {
				return String.format("http://www.euro-millions.com/results/%s", formatter.format(arg0));
			}
		});
	}
	
	public static  String getResult(String uri){
		WebResource resource = Client.create().resource( uri );
		ClientResponse response = resource
				.get( ClientResponse.class );
		String result = response.getEntity( String.class ) ;
		response.close();
		return result;
	}
	public static List<String> getNumbers(String resultPage){
		Document doc = Jsoup.parse(resultPage);
		Element twitterResult = doc.select(".twitter-share-button").first().getAllElements().first();
		String d = twitterResult.toString().replaceAll(".*Numbers:", "").replaceAll("Raffle.*|\".*", "");
		Iterable<String> nums = Splitter.on(Pattern.compile(",|:|;")).trimResults().omitEmptyStrings().split(d);
		return Lists.newArrayList(Iterables.transform(nums, new Function<String,String>(){
			@Override
			public String apply(String arg0) {
				return CharMatcher.DIGIT.retainFrom(arg0);
			}		
		}));
	}
	public static boolean hadWinner(String resultPage){
		return ! resultPage.contains("Rollover");
	}
	private static String LETTER_TEMPLATE = "match (prev:Letter {LATEST:true}),"
				+ "(m1:Number),"
				+ "(m2:Number),"
				+ "(m3:Number),"
				+ "(m4:Number),"
				+ "(m5:Number),"
				+ "(l1:Number),"
				+ "(l2:Number) "
				+ "where m1.value=<m1> and m2.value=<m2> and m3.value=<m3> and m4.value=<m4> and m5.value=<m5>"
				+ " and l1.value=<l1> and l2.value=<l2>"
				+ " create (n:Letter {LATEST:true, date:\"<date>\", hadWinner:<winner>} ) "
				+ " create (n)-[:MAIN {order:1}]->(m1) "
				+ "create (n)-[:MAIN {order:2}]->(m2) "
				+ "create (n)-[:MAIN {order:3}]->(m3) "
				+ "create (n)-[:MAIN {order:4}]->(m4) "
				+ "create (n)-[:MAIN {order:5}]->(m5) "
				+ "create (n)-[:LUCKY {order:1}]->(l1) "
				+ "create (n)-[:LUCKY {order:2}]->(l2) "
				+ "create (n)-[:PREVIOUS]->(prev) "
				+ "return Id(n)";
	public static String createLetter(String uri){
		ST letterTemplate = new ST(LETTER_TEMPLATE);
		String date = Iterables.getLast(Splitter.on("/").omitEmptyStrings().split(uri));
		String page = DataSlurper.getResult(uri);
		boolean winner = DataSlurper.hadWinner(page);
		List<String> nums = DataSlurper.getNumbers(page);
		letterTemplate.add("winner", winner)
		.add("m1", nums.get(0))
		.add("m2", nums.get(1))
		.add("m3", nums.get(2))
		.add("m4", nums.get(3))
		.add("m5", nums.get(4))
		.add("l1", nums.get(5))
		.add("l2", nums.get(6))
		.add("date",date);
		return letterTemplate.render();
	}
}

