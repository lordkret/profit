package com.willautomate.profit.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.willautomate.profit.api.Letter;

public class Analysis {

    private static Logger log = LoggerFactory.getLogger(Analysis.class);
    private  ConcurrentMap<String,Integer> letterFrequency = Maps.newConcurrentMap();
    private ConcurrentMap<Double,Integer> characterFrequency = Maps.newConcurrentMap();

    private static ConcurrentMap<String,Analysis> instances = Maps.newConcurrentMap();

    private Path characterPath;
    private Path letterPath;

    public static synchronized Analysis getInstance(String name){
        Analysis instance = instances.get(name);
        if(instance==null){
            instance =new Analysis(name);
            instances.put(name,instance);
        }
        return instance;
    }

    private Analysis(String name) {
        characterPath = Paths.get(name+"CharacterAna");
        letterPath = Paths.get(name+"LetterAna");
        try {
        if (! Files.exists(characterPath))
            Files.createFile(characterPath);
        if (! Files.exists(letterPath))
        Files.createFile(letterPath);
        } catch (IOException e){
            log.error("Can't create files", e);
        }
    }

    public synchronized void analysis(final Double[] letter){
        
        Double[] letterValue = letter;
        Arrays.sort(letterValue);
        String key = Arrays.toString(letter);
        letterFrequency.put(key,(letterFrequency.get(key)==null)? 1 : letterFrequency.get(key).intValue()+1);
        for(Double character: letter){
            characterFrequency.put(character,(characterFrequency.get(character)==null)? 1: characterFrequency.get(character).intValue()+1);
        }
        try {
        serialize();
        } catch (IOException e){
            log.error("Can't serialize",e);
        }
    }

    private void serialize() throws IOException{
        Files.write(characterPath,serialize(characterFrequency).getBytes(), StandardOpenOption.WRITE);
        Files.write(letterPath,serialize(letterFrequency).getBytes(), StandardOpenOption.WRITE);
        
    }
    private String serialize(Map map){
        return Joiner.on("\n").withKeyValueSeparator(",").join(map.entrySet());
    }
}
