package com.willautomate.profit;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.api.WordFactory;
import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.BasicWord;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

public class ElmanWordDetectorTest {

    @Test
    public void trainingTest(){
        ElmanWordDetector network = new ElmanWordDetector();
        Letter<Double> l1 = new BasicLetter<Double>(ArrayUtils.toArray(23D,29D,31D,39D,44D,5D,8D));
        Letter<Double> l2 = new BasicLetter<Double>(ArrayUtils.toArray(3D,7D,12D,13D,25D,5D,8D));
        Letter<Double> l3 = new BasicLetter<Double>(ArrayUtils.toArray(2D,15D,28D,31D,37D,4D,6D));
        Word w = new BasicWord(l1,l2,l3);
        network.train(w);
        System.out.println(network.predict(l1));
    }
}
