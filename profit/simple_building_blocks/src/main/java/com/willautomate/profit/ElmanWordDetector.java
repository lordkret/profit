package com.willautomate.profit;

import java.nio.file.Path;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.api.WordFactory;
import com.willautomate.profit.api.WordsDetector;

public class ElmanWordDetector implements WordsDetector{

	private BasicNetwork network;


    public static final Double MAX_ERROR = 0.0000000008;
    public static final int LETTER_SIZE = 50;
    
    public BasicNetwork createNetwork(int letterSize, int hiddenLayerSize) {
        ElmanPattern pattern = new ElmanPattern();
        pattern.setInputNeurons(LETTER_SIZE);
        pattern.addHiddenLayer(hiddenLayerSize);
        pattern.setOutputNeurons(LETTER_SIZE);
        pattern.setActivationFunction(new ActivationSigmoid());
        return (BasicNetwork)pattern.generate();
    }
    
    
	public void train(Word word) {
		if (network == null){
			network = createNetwork(word.getLetters()[0].size(),word.size());
		}
		EncogUtility.trainToError(network, WordFactory.toDataSet(word), MAX_ERROR);
		
	}

	public Letter<?> predict(Letter<?> lastLetter) {
		return WordFactory.toLetter(network.compute(WordFactory.toData(lastLetter)));
	}

	public void save(Path location) {
		EncogDirectoryPersistence.saveObject(location.toFile(), network);
		
	}

	public void load(Path location) {
		network = (BasicNetwork) EncogDirectoryPersistence.loadObject(location.toFile());
		
	}

}
