package com.willautomate.profit;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.ArrayUtils;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.train.prop.OpenCLTrainingProfile;
import org.encog.engine.opencl.EncogCLDevice;
import org.encog.neural.data.NeuralDataPair;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.neural.networks.training.strategy.StopTrainingStrategy;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.neural.pattern.JordanPattern;
import org.encog.neural.pattern.NeuralNetworkPattern;
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
	private boolean notRandom = false;
	private double weightValue = Double.MAX_VALUE;
	public BasicNetwork createNetwork(int letterSize, int hiddenLayerSize) {
		NeuralNetworkPattern pattern;

		pattern = new FeedForwardPattern();


		pattern.setInputNeurons(letterSize);
		pattern.addHiddenLayer(hiddenLayerSize*2+880);
		pattern.setOutputNeurons(letterSize);
		pattern.setActivationFunction(new ActivationSigmoid());
		BasicNetwork result = (BasicNetwork) pattern.generate();
		if (notRandom){
			int size = result.getStructure().getFlat().getWeights().length;
			result.getStructure().getFlat().decodeNetwork(WeightsDispatcher.weights(size));
			weightValue = result.getStructure().getFlat().getWeights()[0];
		}
		return result;
	}
	private boolean doesRememberEverything(NeuralDataSet data,int... checks){
		boolean result = false;
		final Iterator<NeuralDataPair> pairs = data.iterator();
		NeuralDataPair pair = null;

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
	
	private synchronized static OpenCLTrainingProfile getProfile(){
		EncogCLDevice dev = Beemo.getDevice();
		String profile = dev.toString();
		String curN = Thread.currentThread().getName();
		if (curN.endsWith("-CPU") || curN.endsWith("-GPU")){
			curN = curN.split("-.PU")[0];
		}
		Thread.currentThread().setName(curN+"-"+profile.substring(0, 3));
		return new OpenCLTrainingProfile(dev);
	}
	private String pattern;
	public boolean train(Word word) {
		if (network == null){
			network = createNetwork(word.getLetters()[0].size(),word.size());
		}

		NeuralDataSet set = WordFactory.toDataSet(word);
		OpenCLTrainingProfile profile = getProfile();
		pattern = profile.getDevice().toString();
		final Train trainMain = new ResilientPropagation(network, set, profile); 
		

				
		double error = 50;
		while (!doesRememberEverything(set,50) && error > 0) {
			//			EncogUtility.trainToError(network, set, error);
			StopTrainingStrategy stop = new StopTrainingStrategy(0.001, 100);
			trainMain.addStrategy(stop);
			while (! stop.shouldStop()){
				trainMain.iteration();
			}
			log.debug("error {}",trainMain.getError());
			error--;
		}
		trainMain.finishTraining();
		log.info("tried {} times",100-error);
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
//		EncogDirectoryPersistence.saveObject(location.toFile(), network);

	}

	public void load(Path location) {
//		network = (BasicNetwork) EncogDirectoryPersistence.loadObject(location.toFile());

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
	public String getPattern() {
		return pattern;
	}
}
