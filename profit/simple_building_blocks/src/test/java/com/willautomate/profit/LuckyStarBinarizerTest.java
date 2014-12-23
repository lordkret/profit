package com.willautomate.profit;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import org.encog.ml.data.basic.BasicMLData;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LuckyStarBinarizerTest {

	@Test
	public void test() throws IOException {
		List<String> csvContent = Arrays.asList("L1,L2", "1,7", "8,11", "3,9");
		Path tmp = Files.createTempFile(".", "csv");
		Files.write(tmp, csvContent, Charset.defaultCharset());
		LuckyStarBinarizer binarizer = new LuckyStarBinarizer();
		Iterator bind = binarizer.binarize(tmp).iterator();
		while (bind.hasNext())
			System.out.println(bind.next());

		assertThat(binarizer.deBinarize(new BasicMLData(binarizer.getInputData()[0])), equalTo( Arrays.asList(1, 7)));
	}
}
