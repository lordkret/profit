package com.willautomate.profit;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class LuckyStarBinarizerTest {

	@Test
	public void test() throws IOException {
		List<String> csvContent = Arrays.asList("L1,L2","1,7","8,11","3,9");
		Path tmp = Files.createTempFile(".", "csv");
		Files.write(tmp, csvContent,Charset.defaultCharset());
		for (String line : Files.readAllLines(tmp, Charset.defaultCharset())){
			System.out.println(line);
		}
		Iterator bind = new LuckyStarBinarizer().binarize(tmp).iterator();
		while (bind.hasNext())
			System.out.println(bind.next());
			}

}
