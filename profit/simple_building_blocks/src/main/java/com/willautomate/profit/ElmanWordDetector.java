package com.willautomate.profit;

import java.nio.file.Path;
import java.util.Iterator;

import org.apache.commons.lang3.ArrayUtils;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.CalculateScore;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Greedy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.api.WordFactory;
import com.willautomate.profit.api.WordsDetector;
import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.DoubleBinarizer;

public class ElmanWordDetector implements WordsDetector{

	private BasicNetwork network;


    public static final Double MAX_ERROR = 0.00000008;
    public static final int LETTER_SIZE = 50;
    
    public BasicNetwork createNetwork(int letterSize, int hiddenLayerSize) {
        ElmanPattern pattern = new ElmanPattern();
        pattern.setInputNeurons(LETTER_SIZE);
        pattern.addHiddenLayer(hiddenLayerSize);
        pattern.setOutputNeurons(LETTER_SIZE);
        pattern.setActivationFunction(new ActivationSigmoid());
        return (BasicNetwork)pattern.generate();
    }
    private int pairingComparison ;
    private boolean doesRememberEverything(MLDataSet data){
    	boolean result = true;
    	final Iterator<MLDataPair> pairs = data.iterator();
    	MLDataPair pair = null;
    	final DoubleBinarizer b = new DoubleBinarizer();
    	Letter<Double> ideal = null;
    	Letter<Double> computed = null;
    	pairingComparison = 0;
    	while (((pair = pairs.next())!= null) && result ){
    		computed = new BasicLetter<Double>(b.debinarize(5, network.compute(pair.getInput()).getData()));
    		ideal = new BasicLetter<Double>(b.debinarize(5,ArrayUtils.toObject(pair.getIdeal().getData())));
    		pairingComparison++;
//    		System.out.println("got " + ideal + "and " + computed);
    		result = result && ideal.equals(computed);
    	}
    	return result;
    }
	public void train(Word word) {
		if (network == null){
			network = createNetwork(word.getLetters()[0].size(),word.size());
		}
		MLDataSet set = WordFactory.toDataSet(word);
		final MLTrain trainMain = new Backpropagation(network, WordFactory.toDataSet(word),0.000001, 0.0);

		//trainMain.addStrategy(new Greedy());

		
		
		int epoch = 0;
		while (!doesRememberEverything(set)) {
			trainMain.iteration();
			epoch++;
			System.out.println("Epoch: " + epoch + " error: " + trainMain.getError() + " pairing comparison " + pairingComparison);
		}
		System.out.println("Learned after " + epoch + " epochs");
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
