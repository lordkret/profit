package com.willautomate.profit;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.willautomate.profit.api.Letter;
import com.willautomate.profit.api.Word;
import com.willautomate.profit.impl.BasicLetter;
import com.willautomate.profit.impl.BasicWord;
import com.willautomate.profit.neo4j.Neo4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.collect.Iterables.*;

import java.util.List;

public class WordProvider {
    private static final Logger log = LoggerFactory.getLogger(WordProvider.class);
    public static Word getWord(final int size, final boolean main){
        List<Letter> letters = Lists.newArrayList();
        try {
        for (int i=1; i<= size;i++) {
            letters.add(getLetter(i, main));
        } } catch (IllegalStateException emptyLetter){
        	log.warn("Empty letter received. Word size truncated to {}",size);
        }
        return new BasicWord(Iterables.toArray(letters,Letter.class));
    }

    private static final int SHIFT = 50;
    public static Letter getLetter(final int humbaHumba, final boolean main){
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
        return new BasicLetter<Double>(toArray(transform,Double.class));
    }
    public static int getMaxWordSize(){
    	String query = "match (l:Letter {LATEST:true}) return Id(l)";
    	String result = Neo4j.sendTransactionalCypherQuery(query);
    	List<String> nums = Splitter.on("row").omitEmptyStrings().trimResults().splitToList(result);
        Iterable<Integer> transform = filter(transform(nums, new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                String no = CharMatcher.DIGIT.retainFrom(s);
                if (! no.isEmpty())
                    return new Integer(no);
                else return null;
            }
        }),Predicates.notNull());
        return transform.iterator().next() - SHIFT;
    }
}
