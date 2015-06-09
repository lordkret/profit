package com.willautomate.profit.daemon;

public class BMO {


	public static void main(String[] args){
		try {
			DataVomiter.vomitData();
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
}
