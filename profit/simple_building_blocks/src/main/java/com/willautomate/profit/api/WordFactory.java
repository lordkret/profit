package com.willautomate.profit.api;

import com.google.common.collect.Lists;
import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.BasicWord;
import com.willautomate.profit.impl.DoubleBinarizer;
import org.apache.commons.lang3.ArrayUtils;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.supercsv.io.CsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WordFactory {

	public static MLDataSet toDataSet(Word word){
		Letter<Double>[] letters = word.getLetters();
		double[][] input = new double[letters.length-1][letters[0].getRawData().length];
		double[][] ideal = new double[letters.length-1][letters[0].getRawData().length];		
		
		for (int i = 1; i<= letters.length-1;i++){
			input[i-1] = ArrayUtils.toPrimitive(letters[i-1].getRawData());
			ideal[i-1] = ArrayUtils.toPrimitive(letters[i].getRawData());
		}
		MLDataSet data = new BasicMLDataSet(input, ideal);
		return data;
		
	}

	
	public static MLData toData(Letter<?> lastLetter) {
		Object[] mlData =  lastLetter.getRawData();
		double[] d = ArrayUtils.toPrimitive(Arrays.copyOf(mlData,mlData.length,Double[].class));
		return new BasicMLData(d);
	}

	public static Letter<Double> toLetter(MLData compute) {
		return new BasicLetter<Double>(ArrayUtils.toObject(compute.getData()));
	}

	public static Word fromCsv(Path csvFile, int startRow, int rowsToRead, String...columnsToRead) throws IOException{
		List<Letter> letters = Lists.newArrayList();
		BufferedReader file = Files.newBufferedReader(csvFile,Charset.defaultCharset());
		CsvMapReader csvReader = new CsvMapReader(file, CsvPreference.EXCEL_PREFERENCE);
		String[] allHeaders = csvReader.getHeader(true);
		double[] binarizedNumbers;
		Map<String,String> oneRow;
		List<String> headersToUse = Arrays.asList(columnsToRead);
		DoubleBinarizer binarizer = new DoubleBinarizer();
		int readRows = 0;
		while ((readRows < rowsToRead)&&(oneRow = csvReader.read(columnsToRead))!= null){
			if (csvReader.getRowNumber() >= startRow){
			letters.add(new BasicLetter(binarizer.binarize(50, oneRow.values())));
			readRows++;
			}
		}
		csvReader.close();
		file.close();
		Word result = new BasicWord(letters.toArray(new Letter[rowsToRead]));
		return result;
	}
}
