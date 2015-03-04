package com.willautomate.profit;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.ArrayUtils;
import org.encog.engine.network.activation.ActivationLOG;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.JordanPattern;
import org.encog.neural.pattern.NeuralNetworkPattern;
import org.encog.persist.EncogDirectoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.api.WordFactory;
import com.willautomate.profit.api.WordsDetector;
import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.DoubleBinarizer;
import com.willautomate.profit.impl.DoubleLetterDistance;

public class ElmanWordDetector implements WordsDetector{

	private BasicNetwork network;

	private static Logger log = LoggerFactory.getLogger(ElmanWordDetector.class);
	public ElmanWordDetector(final int debinarizedLetterSize){
	    this.debinarizedLetterSize = debinarizedLetterSize;
	}
	private final int debinarizedLetterSize;
	private static AtomicBoolean flag = new AtomicBoolean(false);
    
	public BasicNetwork createNetwork(int letterSize, int hiddenLayerSize) {
	    NeuralNetworkPattern pattern;
	    
	        pattern = new ElmanPattern();
	    
	    
        pattern.setInputNeurons(letterSize);
        pattern.addHiddenLayer(hiddenLayerSize);
        pattern.setOutputNeurons(letterSize);
        pattern.setActivationFunction(new ActivationLOG());
        return (BasicNetwork)pattern.generate();
    }
    private boolean doesRememberEverything(MLDataSet data){
    	boolean result = true;
    	final Iterator<MLDataPair> pairs = data.iterator();
    	MLDataPair pair = null;
    	
    	Letter<Double> ideal = null;
    	Letter<Double> computed = null;
    	
    	while (((pair = pairs.next())!= null) ){
    	    Letter<Double> toCompute = new BasicLetter<Double>(ArrayUtils.toObject(pair.getInput().getData()));
    		computed =  (Letter<Double>) predict(toCompute);
    		ideal = new BasicLetter<Double>(ArrayUtils.toObject(pair.getIdeal().getData()));
    		double distance = DoubleLetterDistance.calculate(computed, ideal, debinarizedLetterSize);
    		result = result && (distance == 0);
    		log.debug("distance {} effect of {}",distance,Arrays.toString(DoubleBinarizer.debinarize(debinarizedLetterSize,toCompute.getRawData())));
    	}
    	return result;
    }
	public void train(Word word) {
		if (network == null){
			network = createNetwork(word.getLetters()[0].size(),word.size());
		}
		
		MLDataSet set = WordFactory.toDataSet(word);

		final MLTrain trainMain = new ResilientPropagation((ContainsFlat)network, set); 

//		trainMain.addStrategy(new Greedy());
		
		while (!doesRememberEverything(set)) {
//			EncogUtility.trainToError(network, set, 0.00007);
			trainMain.iteration();
			log.debug("error {}",trainMain.getError());
		}
		trainMain.finishTraining();
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
	public void clean(){
		network = null;
	}

	@Override
	public String toString(){
	    return String.format("Network %s\n weights %s", network.toString(),network.dumpWeights());
	}
}
