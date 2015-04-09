package com.willautomate.profit.api;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.commons.lang3.ArrayUtils;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.junit.Test;

import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.BasicWord;

public class WordFactoryTest {

	@Test
	public void testToDataSet() {
		Letter<Double> l1 = new BasicLetter<Double>(ArrayUtils.toArray(1D,2D));
		Letter<Double> l2 = new BasicLetter<Double>(ArrayUtils.toArray(3D,4D));
		Letter<Double> l3 = new BasicLetter<Double>(ArrayUtils.toArray(5D,6D));
		Letter<Double> l4 = new BasicLetter<Double>(ArrayUtils.toArray(7D,8D));
		Word w = new BasicWord(l1,l2,l3,l4);

		MLDataSet set = WordFactory.toDataSet(w);
		MLDataPair ha = null;
		Iterator<MLDataPair> hi = set.iterator();
		while ((ha =  hi.next()) != null){
			System.out.println(ha);
		}
	}
	
	@Test
	public void testToData() {
		Letter<Double> l = new BasicLetter<Double>(ArrayUtils.toArray(1D,2D));
		System.out.println(WordFactory.toData(l));
	}
	
	@Test
	public void fromCsvShite() throws IOException{
		String[] ls = {"L1","L2"};
		Word s1 = WordFactory.fromCsv(11, Paths.get("src/test/resources/data.csv"), 3, 5, ls);
		Word s2 = WordFactory.fromCsv(11, Paths.get("src/test/resources/data.csv"), 1, 4, ls);
		System.out.println(s1);
		System.out.println(s2);
		
		
	}

}
