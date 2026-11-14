package org.humanjourney.core.mapfeature;
import java.util.*;
import com.google.gson.JsonParser;
public final class FeatureCatalog {
 public record Source(String resource,String pack,String json){}
 public record Data(Map<String,MapFeature> features,FeatureIndex index){public Data{features=Map.copyOf(features);}}
 public record State(Data data,long revision,String lastSuccess,String error){}
 private static volatile State state=initial();private static State initial(){return new State(prepare(List.of()),0,"never","");}
 public static State state(){return state;}
 public static Data prepare(List<Source> sources){if(sources.size()>2048)throw new IllegalArgumentException("maximum 2048 feature resources");var map=new TreeMap<String,MapFeature>();for(var s:sources)try{if(s.json().length()>65536)throw new IllegalArgumentException("feature exceeds 64 KiB characters");var f=MapFeature.parse(JsonParser.parseString(s.json()));if(map.putIfAbsent(f.view().id(),f)!=null)throw new IllegalArgumentException("duplicate stable feature ID: "+f.view().id());}catch(RuntimeException e){throw new IllegalArgumentException(s.resource()+" ["+s.pack()+"]: "+e.getMessage(),e);}return new Data(map,new FeatureIndex(map.values().stream().map(MapFeature::view).toList()));}
 public static void accept(Data data){state=new State(data,state.revision()+1,java.time.Instant.now().toString(),"");}
 public static void reject(String error){var old=state;state=new State(old.data(),old.revision(),old.lastSuccess(),error);}
 public static void reset(){state=initial();}
 public static Map<String,MapFeature.View> project(Data data,Set<String> discovered){var result=new TreeMap<String,MapFeature.View>();for(String id:discovered){var f=data.features().get(id);if(f!=null)result.put(id,f.view());}return Map.copyOf(result);}
 private FeatureCatalog(){}
}
