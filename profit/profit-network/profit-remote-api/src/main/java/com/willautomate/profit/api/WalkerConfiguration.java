package com.willautomate.profit.api;

import java.nio.channels.NetworkChannel;

public class WalkerConfiguration {
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getMinWordSize() {
		return minWordSize;
	}

	public void setMinWordSize(int minWordSize) {
		this.minWordSize = minWordSize;
	}

	public int getMaxWordSize() {
		return maxWordSize;
	}

	public void setMaxWordSize(int maxWordSize) {
		this.maxWordSize = maxWordSize;
	}

	public int getMaxError() {
		return maxError;
	}

	public void setMaxError(int maxError) {
		this.maxError = maxError;
	}
	
	public NetworkPattern getPattern(){
		return pattern;
	}

	public void setPattern(NetworkPattern pattern){
		this.pattern = pattern;
	}
	
	String name;
	String id;
	
	int minWordSize;
	int maxWordSize;
	
	int maxError;

	NetworkPattern pattern;
	
	public static enum NetworkPattern {
		Elmann, 
		ElmannStep,
		Jordan,
		Basic;
		
	}
	
}
