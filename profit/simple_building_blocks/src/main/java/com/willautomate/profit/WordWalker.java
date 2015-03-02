package com.willautomate.profit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.api.WordFactory;
import com.willautomate.profit.impl.DoubleBinarizer;
import com.willautomate.profit.impl.DoubleLetterDistance;

public class WordWalker implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(WordWalker.class);
    private final String[] wordDataPattern;
    private final int binarizedLetterSize;
    private final int debinarizedLetterSize;
    private int startSize = 29;
    private int maxSize = 36;

    private boolean writeToFileOnMinimal = false;
    private Path csv =  Paths.get("src/main/resources/fulldata.csv");
    public WordWalker(int binarizedWordSize, int debinarizedWordSize, String[] wordDataPattern) {
        this.wordDataPattern = wordDataPattern;
        this.binarizedLetterSize = binarizedWordSize;
        this.debinarizedLetterSize = debinarizedWordSize;
     }
    public WordWalker withStartSize(final int wordStartSize){
        this.startSize = wordStartSize;
        return this;
    }
    
    public WordWalker withMaxSize(final int wordMaxSize){
        this.maxSize = wordMaxSize;
        return this;
    }
    
    public WordWalker withDataFile(Path csv){
        this.csv = csv;
        return this;
    }
    String distancePattern = "profitDistance";
    
    public WordWalker withDistancePattern(final String distancePattern){
        this.distancePattern = distancePattern;
        return this;
    }
    public WordWalker writeToFileOnMinimalDistance(final boolean flag){
        this.writeToFileOnMinimal = flag;
        return this;
    }
    private int maximumError = 0;
    public WordWalker withMaximumError(final int error){
        this.maximumError = error;
        return this;
    }
    protected static synchronized Path createMinimalDistanceFile(String pattern) throws IOException{
        Path minimalDistanceF = Paths.get(String.format("minimal-%s",pattern));
        if (!Files.exists(minimalDistanceF))
            Files.createFile(minimalDistanceF);
        return minimalDistanceF;
    }
    @Override
    public void run() {
        String distancePostfix = Thread.currentThread().getName();
        try {
            ElmanWordDetector network = new ElmanWordDetector(debinarizedLetterSize);
            
            Path profitDistance = Paths.get(String.format("%s-%s", distancePattern,distancePostfix));
            if (!Files.exists(profitDistance))
                Files.createFile(profitDistance);
            Path minimumDistanceF = createMinimalDistanceFile(distancePattern);
            boolean wordDone = false;
            StringBuilder builder = new StringBuilder();
            double minimalDistance = 10;
            int wordSize = startSize;
            Letter<Double> toPredict = WordFactory.fromCsv(binarizedLetterSize,csv, 1, 1, wordDataPattern).getLetters()[0];
            
            while (!wordDone) {
                Word p = WordFactory.fromCsv(binarizedLetterSize,csv, 3, wordSize, wordDataPattern);
                network.clean();
                network.train(p);
                Letter<Double> letterToUser = p.getLetters()[p.getLetters().length - 1];
                Letter<Double> predicted = (Letter<Double>) network.predict(letterToUser);

                double distance = DoubleLetterDistance.calculate(toPredict, predicted, debinarizedLetterSize);
                double[] predictedData = ArrayUtils.toPrimitive(DoubleBinarizer.debinarize(debinarizedLetterSize, predicted.getRawData()));
                if (distance < minimalDistance) {
                    minimalDistance = distance;
                    log.warn("Current minimal distance {} and word size {}", minimalDistance, wordSize);
                    if (writeToFileOnMinimal){
                        network.save(Paths.get(String.format("net-%s-ws-%s-%s-%s",distance,wordSize, distancePattern,distancePostfix)));    
                    }
                }
                wordDone = distance <= maximumError;
                Arrays.sort(predictedData);
                builder.append(distance + ",");
                if (wordSize < maxSize) {
                    wordSize++;
                } else {
                    wordSize = startSize;
                }
                log.info("Current word size " + wordSize + " \n toPredict: " + Arrays.toString(DoubleBinarizer.debinarize(debinarizedLetterSize, toPredict.getRawData())) + "\n predicted: "
                        + Arrays.toString(predictedData) + " and distance: " + distance);
                log.debug("Letter used {}", letterToUser);
            }
            builder.append("\n");
            log.warn("Writing {} to file", builder.toString());
            Files.write(profitDistance, builder.toString().getBytes(), StandardOpenOption.APPEND);
            Files.write(minimumDistanceF, String.format("%s,%s", minimalDistance, wordSize).getBytes(), StandardOpenOption.APPEND);

            network.save(Paths.get(String.format("net-final-%s-ws-%s-%s", distancePattern,wordSize,distancePostfix)));
        } catch (Exception e) {
            log.error("Issue occured", e);
        }
    }
}
