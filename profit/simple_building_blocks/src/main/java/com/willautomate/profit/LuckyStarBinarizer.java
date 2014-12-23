package com.willautomate.profit;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.util.kmeans.Centroid;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import com.google.common.collect.Lists;

public class LuckyStarBinarizer {


	
	List<double[]> dataSet = Lists.newArrayList();
	
	public MLDataSet binarize(Path csvFile) throws IOException{
		
		BufferedReader file = Files.newBufferedReader(csvFile,Charset.defaultCharset());
		CsvMapReader csvReader = new CsvMapReader(file, CsvPreference.EXCEL_PREFERENCE);
		String[] hd = csvReader.getHeader(true);
		double[] binarizedNumbers;
		Map<String,String> oneRow;
		while ((oneRow = csvReader.read(hd))!= null){
		binarizedNumbers = new double[11];
		for (Entry s : oneRow.entrySet()){
			binarizedNumbers[Integer.valueOf((String)s.getValue())-1] = 1; 
		}
		dataSet.add(binarizedNumbers);
		}
		csvReader.close();
		file.close();
		return new BasicMLDataSet(getInputData(), getOutputData());
	}

	public static final double THRESHOLD = 0.39;
	public List<Integer> deBinarize(MLData data){
		final List<Integer> result = Lists.newArrayList();
		for (Integer i=0; i< data.size();i++){
			if (data.getData(i) > THRESHOLD){
				result.add(i+1);
			}
		}
		return result;
	}
	
	public double[][] getOutputData() {
		List<double[]> output = dataSet.subList(1, dataSet.size());
		return (double[][]) output.toArray(new double[output.size()-1][]);
		
	}

	public double[][] getInputData(){
		List<double[]> output = dataSet.subList(0, dataSet.size()-1);
		return (double[][]) output.toArray(new double[output.size()-1][]);
	}
	
	
}
