package com.willautomate.profit;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import com.google.common.collect.Lists;
import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.LetterFactory;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.api.WordFactory;
import com.willautomate.profit.impl.BasicLetter;

public class NotBinarizedCharactersWalker {
	private ElmanWordDetector network;
	
	private Word dataSet;
	
	private ElmanWordDetector getNetwork(){
		if (network == null){
			
		}
		return network;
	}
	String file;
	private void populateFullWord() throws IOException{
		CsvMapReader reader = new CsvMapReader(Files.newBufferedReader(Paths.get(file),Charset.defaultCharset()), CsvPreference.EXCEL_PREFERENCE);
		String[] headers = reader.getHeader(true);
		Map<String,String> line = null;
		List<Letter> letter = Lists.newArrayList();
		while ((line = reader.read(headers))!= null) {
//			new BasicLetter<Double>(line.values().toArray())
		}
		
	}
	private Word getWord(){
		if (dataSet == null){
			
		}
		return dataSet;
	}
	
	private int startSize = 1;
	private int maxSize = 122;
	boolean wordDone = false;
	public void walk(){
		while (! wordDone){
			
		}
	}
}
