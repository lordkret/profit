package com.willautomate.profit.daemon;

import java.text.ParseException;
import java.util.List;

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
		String uri = DataSlurper.getUris().get(0);
		String page = DataSlurper.getResult(uri);
		List<String> numbers = DataSlurper.getNumbers(page);
		System.out.println(numbers);
	}
}
