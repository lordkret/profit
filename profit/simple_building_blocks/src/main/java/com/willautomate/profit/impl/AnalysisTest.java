package com.willautomate.profit.impl;

import static org.junit.Assert.*;

import org.junit.Test;

public class AnalysisTest {

    @Test
    public void test() {
        Analysis.getInstance("lame").analysis(new BasicLetter<Double>(1D,2D,4D));
        Analysis.getInstance("lame").analysis(new BasicLetter<Double>(1D,3D,2D));
        Analysis.getInstance("lame").analysis(new BasicLetter<Double>(1D,3D,2D));
    }

}
