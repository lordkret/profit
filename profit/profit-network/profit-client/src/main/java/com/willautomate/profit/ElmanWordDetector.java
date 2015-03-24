package com.willautomate.profit;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.ArrayUtils;
import org.encog.engine.network.activation.ActivationLOG;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.Greedy;
import org.encog.ml.train.strategy.HybridStrategy;
import org.encog.ml.train.strategy.StopTrainingStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.NeuralNetworkPattern;
import org.encog.persist.EncogDirectoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.WalkerConfiguration.NetworkPattern;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.api.WordFactory;
import com.willautomate.profit.api.WordsDetector;
import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.DoubleBinarizer;
import com.willautomate.profit.impl.DoubleLetterDistance;

public class ElmanWordDetector implements WordsDetector{

	private BasicNetwork network;

	private static Logger log = LoggerFactory.getLogger(ElmanWordDetector.class);
	public ElmanWordDetector(final int debinarizedLetterSize, NetworkPattern pattern){
		this.debinarizedLetterSize = debinarizedLetterSize;
		this.netPattern = pattern;
	}
	private final int debinarizedLetterSize;
	private static AtomicBoolean flag = new AtomicBoolean(false);
	private NetworkPattern netPattern;
	public BasicNetwork createNetwork(int letterSize, int hiddenLayerSize) {
		NeuralNetworkPattern pattern = null;
		switch (this.netPattern) {
		case Elmann:
			pattern = new ElmanPattern();
			break;
		case ElmannStep:
			pattern = new ElmannPatternStep();
		default:
			break;
		}

		pattern.setInputNeurons(letterSize);
		pattern.addHiddenLayer(hiddenLayerSize);
		pattern.setOutputNeurons(letterSize);
		pattern.setActivationFunction(new ActivationLOG());
		return (BasicNetwork)pattern.generate();
	}
	private boolean doesRememberEverything(MLDataSet data){
		boolean result = true;
//		final Iterator<MLDataPair> pairs = data.iterator();
//		MLDataPair pair = null;
//
//		Letter<Double> ideal = null;
//		Letter<Double> computed = null;
//	
//		
//		
//		while (((pair = pairs.next())!= null) ){
//			Letter<Double> toCompute = new BasicLetter<Double>(ArrayUtils.toObject(pair.getInput().getData()));
//			computed =  (Letter<Double>) predict(toCompute);
//			ideal = new BasicLetter<Double>(ArrayUtils.toObject(pair.getIdeal().getData()));
//			log.info("Ideal \n{} and computed: \n{}",ideal,DoubleBinarizer.normalize(ArrayUtils.toPrimitive(computed.getRawData()), 1.0));
//			List<Integer> distances = DoubleLetterDistance.calculate(pair.getIdealArray(),DoubleBinarizer.normalize(ArrayUtils.toPrimitive(computed.getRawData()), 1.0) );
//			result = result && (distances.size() == 0);
//			log.info("distance {} effect of {}",distances.size(),Arrays.toString(DoubleBinarizer.debinarize(debinarizedLetterSize,toCompute.getRawData())));
//		}
//				if (result)
//		log.warn("I remember it ({}) all {}",data.getRecordCount(),result);
		return result;
	}
	public void train(Word word) {
		if (network == null){
			network = createNetwork(word.getLetters()[0].size(),Math.max(word.size(),40));
		}

		MLDataSet set = WordFactory.toDataSet(word);

		MLTrain trainMain = null;
		MLTrain trainAlt = null ;
		if (this.netPattern == NetworkPattern.ElmannStep){
//			log.warn("Using Step");
			trainMain  = new ResilientPropagation(network,set); 
			trainAlt =  new NeuralSimulatedAnnealing(
					network, new MatchScore(set), 100, 0, 100);


			trainAlt.addStrategy(new HybridStrategy(trainMain));

		} else {
//			log.warn("Using plain");
			trainAlt = new ResilientPropagation(network,set); 
		}
		trainAlt.addStrategy(new Greedy());
		while ((!doesRememberEverything(set)) ) {

//			EncogUtility.trainToError(network, set, 0);
			final StopTrainingStrategy stop = new StopTrainingStrategy();
			trainAlt.addStrategy(stop);	
			while ((! stop.shouldStop()) ){

				trainAlt.iteration();
				//				log.warn("error {}",trainAlt.getError());

			}
			log.info("error after {}",trainAlt.getError());
		}
		trainAlt.finishTraining();
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
