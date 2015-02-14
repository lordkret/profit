package com.willautomate.profit;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.lang3.ArrayUtils;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Greedy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.simple.EncogUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.api.WordFactory;
import com.willautomate.profit.api.WordsDetector;
import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.DoubleBinarizer;

public class ElmanWordDetector implements WordsDetector{

	private BasicNetwork network;

	private static Logger log = LoggerFactory.getLogger(ElmanWordDetector.class);

    public static final Double MAX_ERROR = 0.00000008;
    public static final int LETTER_SIZE = 50;
    
    public BasicNetwork createNetwork(int letterSize, int hiddenLayerSize) {
    	hiddenLayerSize = 45;
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
    	
    	Letter<Double> ideal = null;
    	Letter<Double> computed = null;
    	pairingComparison = 0;
    	Double[] computedData = null;
    	while (((pair = pairs.next())!= null) && result ){
    	    Letter<Double> toCompute = new BasicLetter<Double>(ArrayUtils.toObject(pair.getInput().getData()));
    		computedData = DoubleBinarizer.debinarize(5,((Letter<Double>) predict(toCompute)).getRawData());
    		Arrays.sort(computedData);
    		computed = new BasicLetter<Double>(computedData);
    		ideal = new BasicLetter<Double>(DoubleBinarizer.debinarize(5,ArrayUtils.toObject(pair.getIdeal().getData())));
    		log.info("Comparing {} to {} as effect of {}",computed,ideal,Arrays.toString(DoubleBinarizer.debinarize(5,toCompute.getRawData())));
    		pairingComparison++;
    		result = result && ideal.equals(computed);
    	}
    	return result;
    }
	public void train(Word word) {
		if (network == null){
			network = createNetwork(word.getLetters()[0].size(),word.size());
		}
		
		MLDataSet set = WordFactory.toDataSet(word);
		final MLTrain trainMain = new ResilientPropagation((ContainsFlat)network, set); 
//				new Backpropagation(network, WordFactory.toDataSet(word),0.000001, 0.0);

//		trainMain.addStrategy(new Greedy());
		
		while (!doesRememberEverything(set)) {
//			EncogUtility.trainToError(network, set, 0.00007);
			trainMain.iteration();
			log.info("pairing comparison: {} and error {}",pairingComparison,trainMain.getError());
		}
		log.info("pairing comparison: {} and error {}",pairingComparison,trainMain.getError());
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
