package com.willautomate.profit.daemon;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

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
}

