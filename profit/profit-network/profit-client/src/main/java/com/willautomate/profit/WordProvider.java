package com.willautomate.profit;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
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

import java.util.List;

public class WordProvider {
    private static final Logger log = LoggerFactory.getLogger(WordProvider.class);
    public static Word getWord(final int size, final boolean main){
        List<Letter> letters = Lists.newArrayList();
        for (int i=1; i<= size;i++) {
            letters.add(getLetter(i, main));
        }
        return new BasicWord(Iterables.toArray(letters,Letter.class));
    }

    private static final int SHIFT = 50;
    private static Letter getLetter(final int humbaHumba, final boolean main){
        String connection = (main)?"MAIN":"LUCKY";
        String query = String.format("match (n:Number)<-[:%s]-(l:Letter) where Id(l) = %s return n.value",connection, SHIFT+humbaHumba);
        log.warn("LEttah: {}",query);
        String letterResult = Neo4j.sendTransactionalCypherQuery(query);
        log.warn("Letter: {}", letterResult);
        List<String> nums = Splitter.on("row").omitEmptyStrings().trimResults().splitToList(letterResult);
        Iterable<Double> transform = Iterables.transform(nums, new Function<String, Double>() {

            @Override
            public Double apply(String s) {
                String no = CharMatcher.DIGIT.retainFrom(s);
                if (! no.isEmpty())
                    return new Double(no);
                else return null;
            }
        });

        log.warn("Letter: {}", transform);
        return new BasicLetter<Double>(Iterables.toArray(Iterables.filter(transform, Predicates.notNull()),Double.class));
    }
}
