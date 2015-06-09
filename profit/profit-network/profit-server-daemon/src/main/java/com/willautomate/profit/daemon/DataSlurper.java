package com.willautomate.profit.daemon;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
public class DataSlurper {

	public static final String BEGGINING_OF_TIME = "10-05-2011";
	private static final Logger log = LoggerFactory.getLogger(DataSlurper.class);
	public static List<String> preSlurp(){
		try {
			return microSlurp(BEGGINING_OF_TIME);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> microSlurp(String lastDate) throws ParseException{
		List<String> commands = Lists.newArrayList();
		for (String uri : getUris(lastDate)){
			log.info("Slurping for {}",uri);
			commands.add(createLetter(uri));
			log.info("Command: {}",Iterables.getLast(commands));
		}
		return commands;
	}
	public static List <String> getUris(){
		try {
			return getUris(BEGGINING_OF_TIME);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static List <String> getUris(String beggining) throws ParseException{
		List<Date> results = Lists.newArrayList();
		Calendar start = Calendar.getInstance();
		final DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy"); 
		start.setTime(formatter.parse(beggining));
		Calendar end = Calendar.getInstance();
		end.setTime(new Date());

		for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 7), date = start.getTime()) {
			results.add(date);
		}
		start.setTime(formatter.parse(beggining));
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
	private static String LETTER_TEMPLATE = "match (prev:Letter {LATEST:true}) with prev "
			+ "(m1:Number {value:<m1>}),"
			+ "(m2:Number {value:<m2>}),"
			+ "(m3:Number {value:<m3>}),"
			+ "(m4:Number {value:<m4>}),"
			+ "(m5:Number {value:<m5>}),"
			+ "(l1:Number {value:<l1>}),"
			+ "(l2:Number {value:<l2>}) "
			+ "remove prev.LATEST "
			+ "create (n:Letter {LATEST:true, date:\"<date>\", hadWinner:<winner>}) "
			+ "create (n)-[:MAIN {order:1}]->(m1) "
			+ "create (n)-[:MAIN {order:2}]->(m2) "
			+ "create (n)-[:MAIN {order:3}]->(m3) "
			+ "create (n)-[:MAIN {order:4}]->(m4) "
			+ "create (n)-[:MAIN {order:5}]->(m5) "
			+ "create (n)-[:LUCKY {order:1}]->(l1) "
			+ "create (n)-[:LUCKY {order:2}]->(l2) "
			+ "create (n)-[:PREVIOUS]->(prev) "
			+ "return Id(n) ";
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

