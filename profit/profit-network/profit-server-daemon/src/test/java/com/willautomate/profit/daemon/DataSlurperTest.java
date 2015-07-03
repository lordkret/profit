package com.willautomate.profit.daemon;

import java.util.List;

import org.junit.Test;

public class DataSlurperTest {

	@Test
	public void testGetUris() {
		System.out.println(DataVomiter.getLastDate());
		
	}

	@Test
	public void testGetResult() {
		for (int i=0; i< 1; i++){
		String uri = DataSlurper.getUris().get(i);
		String page = DataSlurper.getResult(uri);
		List<String> numbers = DataSlurper.getNumbers(page);
		boolean hadWinner = DataSlurper.hadWinner(page);
		System.out.println(numbers + " :: " + hadWinner);
		System.out.println(DataSlurper.createLetter(uri));
		}
	}
	
	@Test public void testLetterId(){
		System.out.println(DataSlurper.getLetterId());
	}
}
