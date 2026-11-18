package org.humanjourney.core.mapfeature;
import java.util.*;
import com.google.gson.JsonParser;
public final class FeatureCatalog {
 public record Source(String resource,String pack,String json){}
 public record Data(Map<String,MapFeature> features,FeatureIndex index){public Data{features=Map.copyOf(features);}}
 public record State(Data data,long revision,String lastSuccess,String error){}
 private static volatile State state=initial();private static State initial(){return new State(prepare(List.of()),0,"never","");}
 public static State state(){return state;}
 public static Data prepare(List<Source> sources){if(sources.size()>2048)throw new IllegalArgumentException("maximum 2048 feature resources");var map=new TreeMap<String,MapFeature>();long vertices=0,rings=0,characters=0;for(var s:sources)try{characters+=s.json().length();if(characters>67108864)throw new IllegalArgumentException("feature data exceeds 64 MiB characters");if(s.json().length()>524288)throw new IllegalArgumentException("feature exceeds 512 KiB characters");var f=MapFeature.parse(JsonParser.parseString(s.json()));vertices+=f.view().geometry().vertexCount();rings+=f.view().geometry().ringCount();if(vertices>FeatureGeometry.MAX_CATALOG_VERTICES||rings>FeatureGeometry.MAX_CATALOG_RINGS)throw new IllegalArgumentException("catalog geometry budget exceeded");if(map.putIfAbsent(f.view().id(),f)!=null)throw new IllegalArgumentException("duplicate stable feature ID: "+f.view().id());}catch(RuntimeException e){throw new IllegalArgumentException(s.resource()+" ["+s.pack()+"]: "+e.getMessage(),e);}GeometryBudget.check(map.values().stream().map(MapFeature::view).toList());return new Data(map,new FeatureIndex(map.values().stream().map(MapFeature::view).toList()));}
 public static void accept(Data data){state=new State(data,state.revision()+1,java.time.Instant.now().toString(),"");}
 public static void reject(String error){var old=state;state=new State(old.data(),old.revision(),old.lastSuccess(),error);}
 public static void reset(){state=initial();}
 public static Map<String,MapFeature.View> project(Data data,Set<String> discovered){var result=new TreeMap<String,MapFeature.View>();for(String id:discovered){var f=data.features().get(id);if(f!=null)result.put(id,f.view());}return Map.copyOf(result);}
 public static String complexity(Data data){long v1=0,v2=0,parts=0,holes=0,vertices=0;var kinds=new TreeMap<String,Long>();MapFeature largest=null;for(var f:data.features().values()){if(f.schemaVersion()==1)v1++;else v2++;var g=f.view().geometry();kinds.merge(g.kind().name().toLowerCase(Locale.ROOT),1L,Long::sum);parts+=g.parts().size();holes+=g.holeCount();vertices+=g.vertexCount();if(largest==null||g.vertexCount()>largest.view().geometry().vertexCount()||g.vertexCount()==largest.view().geometry().vertexCount()&&f.view().id().compareTo(largest.view().id())<0)largest=f;}return "Geometry: schema-v1="+v1+" schema-v2="+v2+" types="+kinds+" parts="+parts+" holes="+holes+" vertices="+vertices+" largest="+(largest==null?"none":largest.view().id()+"/"+largest.view().geometry().vertexCount());}
 private FeatureCatalog(){}
}
