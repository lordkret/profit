package com.willautomate.profit;

import org.junit.Test;

import java.io.IOException;

public class FirstNetworkTest {

    @Test
    public void train() throws IOException {
        Elmann network = new Elmann();
        network.predict(network.train());
        //network.evaluate();
    }
}
