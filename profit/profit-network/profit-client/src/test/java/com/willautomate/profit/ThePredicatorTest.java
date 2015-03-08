package com.willautomate.profit;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import com.google.common.collect.Lists;
import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.api.WordFactory;
import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.DoubleBinarizer;

public class ThePredicatorTest {

	@Test
	public void predict() {
		Letter<Double> lastLetter = new BasicLetter<Double>(
				DoubleBinarizer.binarize(50, 2, 5, 18, 30, 43));
		ElmanWordDetector detector = new ElmanWordDetector(50);
		detector.load(Paths.get("yay1_2_distance"));
		Letter<Double> next = (Letter<Double>) detector.predict(lastLetter);
		System.out.println(Arrays.toString(DoubleBinarizer.debinarize(5,
				next.getRawData())));
		ElmanWordDetector detector2 = new ElmanWordDetector(50);
		detector2.load(Paths.get("yay1_2_distance_2"));
		Letter<Double> next2 = (Letter<Double>) detector2.predict(lastLetter);
		System.out.println(Arrays.toString(DoubleBinarizer.debinarize(5,
				next2.getRawData())));
	}

	@Test
	public void upTrainAndPredict() throws IOException {
		Path csv = Paths.get("src/main/resources/fulldata.csv");
		File dir = Paths.get(".").toFile();

		for (File net : dir.listFiles()) {
			if (net.getName().startsWith("net-final-main0")) {
		
		
			ElmanWordDetector detector = new ElmanWordDetector(50);
			detector.load(Paths.get(net.getAbsolutePath()));
			Word p = WordFactory.fromCsv(50, csv, 1, 31,
					ElmanWordDetectorTest.MAIN_WORD);
			detector.train(p);
			Letter<Double> lastLetter = new BasicLetter<Double>(
					DoubleBinarizer.binarize(50, 3, 25, 28, 34, 50));
			Letter<Double> next = (Letter<Double>) detector.predict(lastLetter);
			Double[] d = DoubleBinarizer.debinarize(5, next.getRawData());
			Arrays.sort(d);
			System.out.println(Arrays.toString(d));
			}
		}
	}

	@Test
	public void upTrainLuckyAndPredict() throws IOException {
		Path csv = Paths.get("src/main/resources/fulldata.csv");
		File dir = Paths.get(".").toFile();

		for (File net : dir.listFiles()) {
			if (net.getName().startsWith("net-final-lucky")) {
				ElmanWordDetector detector = new ElmanWordDetector(11);
				detector.load(Paths.get(net.getAbsolutePath()));
				Word p = WordFactory.fromCsv(11, csv, 1, 5,
						ElmanWordDetectorTest.LUCKY_WORD);
				detector.train(p);
				Letter<Double> lastLetter = new BasicLetter<Double>(
						DoubleBinarizer.binarize(11, 1, 11));
				Letter<Double> next = (Letter<Double>) detector
						.predict(lastLetter);
				Double[] d = DoubleBinarizer.debinarize(2, next.getRawData());
				Arrays.sort(d);
				System.out.println(Arrays.toString(d));
			}
		}
	}
	
	@Test
	public void findMeOutlierDistances() throws IOException{
		Path csv = Paths.get("src/main/resources/fulldata.csv");
		CsvMapReader csvReader = new CsvMapReader(Files.newBufferedReader(csv, Charset.defaultCharset()), CsvPreference.EXCEL_PREFERENCE);
		String[] allHeaders = csvReader.getHeader(true);
		Map<String,String> oneRow;
		List<Integer> numbers = Lists.newArrayList();
		while ((oneRow = csvReader.read("M1", "M2", "M3", "M4", "M5", null, null))!= null){
			System.out.println(oneRow.get("M5"));
			numbers.add(Integer.valueOf(oneRow.get("M5")));
			}
		csvReader.close();
		
		List<Integer> outliers = Lists.newArrayList();
		for (int i=0;i<numbers.size();i++){
			if (numbers.get(i) < 35)
				outliers.add(i);
		}
		
		CsvMapWriter csvWriter = new CsvMapWriter(Files.newBufferedWriter(Paths.get("src/main/resources/outliers.csv"), Charset.defaultCharset(), StandardOpenOption.WRITE,StandardOpenOption.CREATE_NEW), CsvPreference.EXCEL_PREFERENCE);
		csvReader = new CsvMapReader(Files.newBufferedReader(csv, Charset.defaultCharset()), CsvPreference.EXCEL_PREFERENCE);
		allHeaders = csvReader.getHeader(true);
		int currentRow;
		while ((oneRow = csvReader.read(allHeaders))!= null){
			currentRow = csvReader.getRowNumber()-1;
			if ( outliers.contains(currentRow)|| outliers.contains(currentRow-1)){
				csvWriter.write(oneRow, allHeaders);
			}
		}
		csvWriter.flush();
		csvWriter.close();
	}
}