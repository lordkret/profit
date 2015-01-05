package com.willautomate.profit.impl;

import java.util.Arrays;
import java.util.List;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;

public class BasicWord implements Word {

	List<Letter<?>> letters;
	
	public int size() {
		return (letters != null)? letters.size() : 0; 

	}

	public Letter<?>[] getLetters() {
		return letters.toArray(new Letter[letters.size()]);
	}

	public BasicWord(Letter<?>... letters){
		this.letters = Arrays.asList(letters);
	}
}
