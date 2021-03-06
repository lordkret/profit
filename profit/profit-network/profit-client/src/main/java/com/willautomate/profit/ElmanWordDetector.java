package com.willautomate.profit;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.ArrayUtils;
import org.encog.engine.network.activation.ActivationStep;
import org.encog.mathutil.randomize.NguyenWidrowRandomizer;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.HybridStrategy;
import org.encog.ml.train.strategy.StopTrainingStrategy;
import org.encog.ml.train.strategy.Strategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.ContainsFlat;
import org.encog.neural.networks.training.TrainingSetScore;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.pso.NeuralPSO;
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

public class ElmanWordDetector {

	private BasicNetwork network;

	private static Logger log = LoggerFactory.getLogger(ElmanWordDetector.class);
	public ElmanWordDetector(final int debinarizedLetterSize){
		this.debinarizedLetterSize = debinarizedLetterSize;
	}
	private final int debinarizedLetterSize;
	private static AtomicBoolean flag = new AtomicBoolean(false);
	private boolean notRandom = false;
	private double weightValue = Double.MAX_VALUE;
	public BasicNetwork createNetwork(int letterSize, int hiddenLayerSize) {
		NeuralNetworkPattern pattern;

		pattern = new JordanPattern();


		pattern.setInputNeurons(letterSize);
		pattern.addHiddenLayer(50000);
		pattern.setOutputNeurons(letterSize);
		pattern.setActivationFunction(new ActivationStep());
		BasicNetwork result = (BasicNetwork) pattern.generate();
		if (notRandom){
			int size = result.getStructure().getFlat().getWeights().length;
			result.getStructure().getFlat().setWeights(WeightsDispatcher.weights(size));
			weightValue = result.getStructure().getFlat().getWeights()[0];
		}
		return result;
	}
	private boolean doesRememberEverything(MLDataSet data,int... checks){
		boolean result = false;
		final Iterator<MLDataPair> pairs = data.iterator();
		MLDataPair pair = null;

		Letter<Double> ideal = null;
		Letter<Double> computed = null;
		int toCheck = 1;
		if (checks.length > 0)
			toCheck = Math.max(checks[0],toCheck);
		while (! result && (toCheck > 0) ){
			while (((pair = pairs.next())!= null) ){
				result = true;
				Letter<Double> toCompute = new BasicLetter<Double>(ArrayUtils.toObject(pair.getInput().getData()));
				computed =  (Letter<Double>) predict(toCompute);
				ideal = new BasicLetter<Double>(ArrayUtils.toObject(pair.getIdeal().getData()));
				log.debug("Comparing {} to ideal {}",DoubleBinarizer.debinarizeAsString(debinarizedLetterSize,computed.getRawData()),DoubleBinarizer.debinarizeAsString(debinarizedLetterSize, ideal.getRawData()));
				double distance = DoubleLetterDistance.calculate(computed, ideal, debinarizedLetterSize);
				result = result && (distance == 0);
				log.debug("distance {} effect of {}",distance,DoubleBinarizer.debinarizeAsString(debinarizedLetterSize,toCompute.getRawData()));
			}
			toCheck--;
		}
		return result;
	}
	public boolean train(Word word, String... fileToSave) {
		if (network == null){
			network = createNetwork(word.getLetters()[0].size(),word.size());
		}
		if (fileToSave.length > 0){
			this.save(Paths.get(fileToSave[0]));
		}
		MLDataSet set = WordFactory.toDataSet(word);

		final MLTrain trainMain = new ResilientPropagation((ContainsFlat)network, set); 
//		final MLTrain trainAMain = new NeuralPSO(network, new NguyenWidrowRandomizer(), new TrainingSetScore(set), 33);
//		trainMain.addStrategy(new HybridStrategy(trainAMain));
				
		double error = 3;
		while (!doesRememberEverything(set,1) && error > 0) {
			//			EncogUtility.trainToError(network, set, error);
			StopTrainingStrategy stop = new StopTrainingStrategy(0.001, 20);
			trainMain.addStrategy(stop);
			while (! stop.shouldStop()){
				trainMain.iteration();
			}
			log.warn("error {}",trainMain.getError());
			error--;
		}
		trainMain.finishTraining();
		log.warn("tried {} times",3-error);
		if (error == 0){
			log.error("I didnt learn");
			return false;
		} else {
			return true;
		}
		
		
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
	public double getWeightValue() {
		return weightValue;
	}
}
