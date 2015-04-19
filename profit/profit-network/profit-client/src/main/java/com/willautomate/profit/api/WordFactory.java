package com.willautomate.profit.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import com.google.common.collect.Lists;
import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.BasicWord;
import com.willautomate.profit.impl.DoubleBinarizer;

public class WordFactory {

	public static NeuralDataSet toDataSet(Word word){
		Letter<Double>[] letters = word.getLetters();
		double[][] input = new double[letters.length-1][letters[0].getRawData().length];
		double[][] ideal = new double[letters.length-1][letters[0].getRawData().length];		
		
		for (int i = 1; i<= letters.length-1;i++){
			input[i-1] = ArrayUtils.toPrimitive(letters[i-1].getRawData());
			ideal[i-1] = ArrayUtils.toPrimitive(letters[i].getRawData());
		}
		NeuralDataSet data = new BasicNeuralDataSet(input, ideal);
		return data;
		
	}

	
	public static NeuralData toData(Letter<?> lastLetter) {
		Object[] mlData =  lastLetter.getRawData();
		double[] d = ArrayUtils.toPrimitive(Arrays.copyOf(mlData,mlData.length,Double[].class));
		return new BasicNeuralData(d);
	}

	public static Letter<Double> toLetter(NeuralData compute) {
		return new BasicLetter<Double>(ArrayUtils.toObject(compute.getData()));
	}

	public static Word fromCsv(int binarizedWordSize, Path csvFile, int startRow, int rowsToRead, String...columnsToRead) throws IOException{
		List<Letter> letters = Lists.newArrayList();
		BufferedReader file = Files.newBufferedReader(csvFile,Charset.defaultCharset());
		CsvMapReader csvReader = new CsvMapReader(file, CsvPreference.EXCEL_PREFERENCE);
		String[] allHeaders = csvReader.getHeader(true);
		double[] binarizedNumbers;
		Map<String,String> oneRow;
		List<String> headersToUse = Arrays.asList(columnsToRead);
		int readRows = 0;
		while ((readRows < rowsToRead)&&(oneRow = csvReader.read(columnsToRead))!= null){
			if (csvReader.getRowNumber() > startRow){
			letters.add(new BasicLetter(DoubleBinarizer.binarize(binarizedWordSize, oneRow.values())));
			readRows++;
			}
		}
		csvReader.close();
		file.close();
		Collections.reverse(letters);
		Word result = new BasicWord(letters.toArray(new Letter[rowsToRead]));
		return result;
	}
}
