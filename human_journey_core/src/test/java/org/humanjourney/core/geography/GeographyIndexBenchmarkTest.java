package org.humanjourney.core.geography;
import java.util.*;
import java.nio.file.*;
import java.lang.reflect.*;
import com.google.gson.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.humanjourney.core.npc.context.NpcGeography;
/** Identical test is copied into exact0.14.2 reference source on the same CI runner. */
class GeographyIndexBenchmarkTest {
 static volatile Object sink;
 static JsonArray ring(int n,double x,double z,double radius){var points=new JsonArray();for(int i=0;i<n;i++){double a=i*2*Math.PI/n;var p=new JsonArray();p.add(x+radius*Math.cos(a));p.add(z+radius*Math.sin(a));points.add(p);}return points;}
 static List<JsonElement> fixture(int count,boolean detail,boolean dense){var resources=new ArrayList<JsonElement>();for(int first=0;first<count;first+=32){var root=new JsonObject();root.addProperty("schema_version",detail?2:1);var areas=new JsonArray();for(int i=first;i<Math.min(first+32,count);i++){double x=dense?0:i%64*400,z=dense?0:i/64*400;var area=new JsonObject();area.addProperty("id","local_"+i);area.addProperty("priority",dense?i:0);area.addProperty("display_label","Local");if(detail){var geometry=new JsonObject();geometry.addProperty("type","polygon");geometry.add("outer",ring(48,x,z,100));var holes=new JsonArray();holes.add(ring(16,x+50,z,10));geometry.add("holes",holes);area.add("geometry",geometry);}else area.add("polygon",ring(4,x,z,100));areas.add(area);}root.add("local_areas",areas);resources.add(root);}return resources;}
 static double median(Runnable task){for(int i=0;i<2;i++)task.run();var samples=new double[5];for(int i=0;i<5;i++){long start=System.nanoTime();task.run();samples[i]=(System.nanoTime()-start)/1e6;}Arrays.sort(samples);return samples[2];}
 static Method method(String name,Class<?>... args){try{return NpcGeography.class.getMethod(name,args);}catch(NoSuchMethodException e){return null;}}
 @Test @Timeout(240) void compareDistributedAndDenseResolution()throws Exception{
  var report=new LinkedHashMap<String,Object>();var indexed=method("candidateCount",String.class,double.class,double.class);var indexTime=method("indexBuildNanos");report.put("resolver",indexed==null?"exact0.14.2 linear":"indexed components");report.put("java",System.getProperty("java.version"));report.put("method","same runner/identical harness;2warmups, median5; repeated lookup batches256; parsing includes validation/index publication, index timing recorded separately by constructor; no gameplay");var results=new LinkedHashMap<String,Object>();
  for(int count:List.of(1500,4096))for(boolean detail:List.of(false,true))for(boolean dense:List.of(false,true)){
   String key=count+"_"+(detail?"v2_detailed":"v1_coarse")+"_"+(dense?"dense":"distributed");var row=new LinkedHashMap<String,Object>();results.put(key,row);row.put("locals",count);row.put("vertices_per_area",detail?64:4);row.put("rings_per_area",detail?2:1);
   if(detail&&indexed==null){row.put("supported",false);row.put("reason","exact0.14.2 does not support geography schema2");continue;}row.put("supported",true);var data=fixture(count,detail,dense);List<String> json=data.stream().map(JsonElement::toString).toList();var geo=NpcGeography.parse(data,Set.of("era_0"));double x=dense?0:400,z=0;assertEquals(dense?"local_"+(count-1):"local_1",geo.resolve("minecraft:overworld",x,z,"era_0").local());
   row.put("parse_validate_publish_ms",median(()->sink=NpcGeography.parse(json.stream().map(JsonParser::parseString).toList(),Set.of("era_0"))));row.put("index_construction_ms",indexTime==null?0:((Number)indexTime.invoke(geo)).longValue()/1e6);row.put("single_resolution_us",median(()->sink=geo.resolve("minecraft:overworld",x,z,"era_0"))*1000);row.put("repeated_resolution_us",median(()->{for(int i=0;i<256;i++)sink=geo.resolve("minecraft:overworld",x,z,"era_0");})*1000/256);row.put("candidates_before_exact",indexed==null?count:indexed.invoke(geo,"minecraft:overworld",x,z));
  }
  report.put("cases",results);var output=Path.of(System.getProperty("humanjourney.projectDir"),"build/reports/geography-index-benchmark.json");Files.createDirectories(output.getParent());Files.writeString(output,new GsonBuilder().setPrettyPrinting().create().toJson(report));
 }
}
