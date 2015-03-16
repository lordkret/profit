package com.willautomate.profit.api;

public class DataConfiguration {
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataSet() {
		return dataSet;
	}

	public void setDataSet(String dataSet) {
		this.dataSet = dataSet;
	}

	public int getBinarizedLetterSize() {
		return binarizedLetterSize;
	}

	public void setBinarizedLetterSize(int binarizedLetterSize) {
		this.binarizedLetterSize = binarizedLetterSize;
	}

	public int getDeBinarizedLetterSize() {
		return deBinarizedLetterSize;
	}

	public void setDeBinarizedLetterSize(int deBinarizedLetterSize) {
		this.deBinarizedLetterSize = deBinarizedLetterSize;
	}

	public LetterPattern getLetterPattern() {
		return letterPattern;
	}

	public void setLetterPattern(LetterPattern letterPattern) {
		this.letterPattern = letterPattern;
	}

		public static enum LetterPattern {
			LUCKY(null,null,null,null,null,"L1","L2"),
			MAIN("M1","M2","M3","M4","M5",null,null);
			
			private String[] pattern;
	    	LetterPattern(String... pattern) {
	    		this.pattern = pattern;
	    	}
	    	
	    	public String[] toPattern(){
	    		return this.pattern;
	    	}

	    }
	
	private String id;
	private String name;
	
	private String dataSet;
	
	private int binarizedLetterSize;
	private int deBinarizedLetterSize;
	
	private LetterPattern letterPattern;
}
