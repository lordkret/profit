package com.willautomate.profit.daemon;

import java.text.ParseException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

public class DataSlurperTest {

	@Test
	public void testGetUris() throws ParseException {
		for (String s: DataSlurper.getUris()){
			System.out.println(s);
		}
	}

	@Test
	public void testGetResult() throws ParseException{
//		System.out.println(DataSlurper.getResult(DataSlurper.getUris().get(0)));
		Document doc = Jsoup.parse(DataSlurper.getResult(DataSlurper.getUris().get(0)));
//		System.out.println(doc);
		Element sc = doc.select(".twitter-share-button").first().getAllElements().first();
		System.out.println(sc.toString().replaceAll(".*Numbers:", "").replaceAll("Raffle:.*", ""));
//		System.out.println(sc.);
		Element numbers = doc.getElementById("jsBallOrderCell");
		Elements el = doc.select(".ordered.orderedFix");
		System.out.println(el);
		numbers.getElementById("ball");
		System.out.println("Numbers in drawn order " + numbers.html());
	}
}
