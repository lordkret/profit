package com.willautomate.profit;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.api.WordFactory;
import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.BasicWord;
import com.willautomate.profit.impl.DoubleBinarizer;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.util.Arrays;

public class ElmanWordDetectorTest {

    DoubleBinarizer binarizer = new DoubleBinarizer();
    @Test
    public void trainingTest(){
        ElmanWordDetector network = new ElmanWordDetector();
        Letter<Double> l1 = new BasicLetter<Double>(binarizer.binarize(50,23, 29, 31, 39, 44));
        Letter<Double> l2 = new BasicLetter<Double>(binarizer.binarize(50,3, 7, 12, 13, 25));
        Letter<Double> l3 = new BasicLetter<Double>(binarizer.binarize(50, 2, 15, 28, 31, 37));
        Letter<Double> l4 = new BasicLetter<Double>(binarizer.binarize(50, 1,3,31,42,46));
        Word w = new BasicWord(l1,l2,l3,l4);
        network.train(w);
        System.out.println(Arrays.deepToString(binarizer.debinarize(5,Arrays.copyOf(network.predict(l1).getRawData(), 50, Double[].class))));
        System.out.println(Arrays.deepToString(binarizer.debinarize(5,Arrays.copyOf(network.predict(l2).getRawData(), 50, Double[].class))));
        System.out.println(Arrays.deepToString(binarizer.debinarize(5,Arrays.copyOf(network.predict(l3).getRawData(), 50, Double[].class))));
        System.out.println(Arrays.deepToString(binarizer.debinarize(5,Arrays.copyOf(network.predict(l4).getRawData(), 50, Double[].class))));
        
        Letter<Double> veryRandom = new BasicLetter<Double>(binarizer.binarize(50, 4,6,28,31,46));
        System.out.println(Arrays.deepToString(binarizer.debinarize(5,Arrays.copyOf(network.predict(veryRandom).getRawData(), 50, Double[].class))));
        
    }
}
