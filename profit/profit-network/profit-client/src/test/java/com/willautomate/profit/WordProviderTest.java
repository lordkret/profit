package com.willautomate.profit;

import org.junit.Test;

import java.util.Arrays;

public class WordProviderTest {

    @Test
    public void testGetWord() throws Exception {
        System.out.println(Arrays.toString(WordProvider.getWord(3, true).getLetters()));
    }
}