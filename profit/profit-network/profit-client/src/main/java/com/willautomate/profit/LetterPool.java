package com.willautomate.profit;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.toArray;
import static com.google.common.collect.Iterables.transform;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.willautomate.profit.api.Letter;
import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.DoubleBinarizer;
import com.willautomate.profit.neo4j.Neo4j;

public class LetterPool  {
	 private static final int SHIFT = 50;
	 private static final Logger log = LoggerFactory.getLogger(LetterPool.class);
	 
	 private static Map<Integer,Letter> mainLetters = Maps.newHashMap();
	 private static Map<Integer,Letter> luckyLetters = Maps.newHashMap();
	 
	public static synchronized Letter getLetter(final int humbaHumba, final boolean main){
		Letter result;
		if (main){
			if ((result = mainLetters.get(humbaHumba)) == null){
				result = fetchLetter(humbaHumba, main);
				mainLetters.put(humbaHumba, result);
			}
		} else {
			if ((result = luckyLetters.get(humbaHumba))==null){
				result = fetchLetter(humbaHumba, main);
				luckyLetters.put(humbaHumba, result);
			}
		}
		return result;
	}
	private static Letter fetchLetter(final int humbaHumba, final boolean main){
        String connection = (main)?"MAIN":"LUCKY";
        String query = String.format("match (n:Number)<-[:%s]-(l:Letter) where Id(l) = %s return n.value",connection, SHIFT+humbaHumba);
        log.warn("LEttah: {}",query);
        String letterResult = Neo4j.sendTransactionalCypherQuery(query);
        log.warn("Letter: {}", letterResult);
        List<String> nums = Splitter.on("row").omitEmptyStrings().trimResults().splitToList(letterResult);
        Iterable<Double> transform = filter(transform(nums, new Function<String, Double>() {
            @Override
            public Double apply(String s) {
                String no = CharMatcher.DIGIT.retainFrom(s);
                if (! no.isEmpty())
                    return new Double(no);
                else return null;
            }
        }),Predicates.notNull());

        log.warn("Letter: {}", transform);
        Preconditions.checkState(! isEmpty(transform), "Letter is empty");
        int bitSize = (main)?50:11;
        return new BasicLetter<Double>(DoubleBinarizer.binarize(bitSize, toArray(transform,Double.class)));

	}
}
