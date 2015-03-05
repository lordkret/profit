package com.willautomate.profit.impl;

import com.google.common.collect.Maps;
import com.willautomate.profit.api.Letter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Hannah on 05/03/2015.
 */
public class Analysis {

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

    private Analysis(String name){
        characterPath = Paths.get(name+"CharacterAna");
        letterPath = Paths.get(name+"LetterAna");
    }

    public synchronized void analysis(Letter<Double> letter){
        String key = Arrays.toString(letter.getRawData());
        letterFrequency.put(key,(letterFrequency.get(key)==null)? 1 : letterFrequency.get(key).intValue()+1);
        for(Double character: letter.getRawData()){
            characterFrequency.put(character,(characterFrequency.get(character)==null)? 1: characterFrequency.get(character).intValue()+1);
        }
    }

    private void serialize(){
        Files.write(characterPath,characterFrequency.entrySet(), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
    }
}
