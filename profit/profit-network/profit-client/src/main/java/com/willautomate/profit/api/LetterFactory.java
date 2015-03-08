package com.willautomate.profit.api;

import com.willautomate.profit.impl.BasicLetter;

public class LetterFactory {

	public static class LetterBuilder<T>{
		private BinarizationMethod<T> binarizer;
		private int bitSize;
		private int[] values;
		public Letter<T> build(){
			return new BasicLetter<T>(binarizer.binarize(bitSize, values));
		}
		public LetterBuilder<T> useBinarizationMethod(BinarizationMethod<T> binarizer){
			this.binarizer = binarizer;
			return this;
		}
		
		public LetterBuilder<T> withBitSize(int bitSize){
			this.bitSize = bitSize;
			return this;
		}
		
		public LetterBuilder<T> withValues(int... values){
			this.values = values;
			return this;
		}
	}
	
	public static <T> LetterBuilder<T> newBuilder(){
		return new LetterBuilder<T>();
		
	}
	public static <T> Letter<T> createLetter(LetterBuilder<T> builder, int... values){
		return builder.withValues(values).build();
	}
	public static <T> LetterBuilder<T> newBinarizingSetSizeBuilder(BinarizationMethod<T> binarizer, int bitSize){
		return new LetterBuilder<T>().withBitSize(bitSize).useBinarizationMethod(binarizer);
	}
	
}
