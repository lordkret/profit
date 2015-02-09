package com.willautomate.profit.impl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;

import java.util.Arrays;
import java.util.List;

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
	
	public String toString(){
		return String.format("a %s", Joiner.on("\n ").join(Collections2.transform(letters, new Function<Letter, String>() {

			@Override
			public String apply(Letter input) {
				Letter<Double> val = input;
				return Arrays.deepToString(DoubleBinarizer.debinarize(Math.min(5, val.size()), val.getRawData()));
			}
		})));
	}
}
