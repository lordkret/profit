package com.willautomate.profit.api;

import java.nio.file.Path;

public interface WordsDetector {

	boolean train(Word word);
	Letter<?> predict(Letter<?> lastLetter);
	
	void save(Path location);
	void load(Path location);
	
}
