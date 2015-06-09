package com.willautomate.profit.daemon;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.Test;

public class DataVomiterTest {

	@Test
	public void test() throws ParseException {
//		System.out.println("safsdf"+DataVomiter.getLastDate());
//		DataVomiter.vomitData();
		System.out.println(DataVomiter.getNextData(DataVomiter.getNextData(DataSlurper.BEGGINING_OF_TIME)));
	}

}
