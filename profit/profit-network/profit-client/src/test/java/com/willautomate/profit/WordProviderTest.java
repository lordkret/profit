package com.willautomate.profit;

import org.junit.Test;

import java.util.Arrays;

public class WordProviderTest {

    @Test
    public void testGetWord() throws Exception {
        System.out.println(Arrays.toString(WordProvider.getWord(3, true).getLetters()));
//        System.out.println(Arrays.toString(WordProvider.getWord(5000, true).getLetters()));
    }
    
    @Test
    public void testGetLetter(){
    	System.out.println(WordProvider.getLetter(7, true));
    }
    
    @Test
    public void testGetMax(){
    	System.out.println(WordProvider.getMaxWordSize());
    }
}