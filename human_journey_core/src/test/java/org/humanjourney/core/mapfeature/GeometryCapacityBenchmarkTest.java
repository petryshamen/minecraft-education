package org.humanjourney.core.mapfeature;
import java.util.*;
import java.nio.file.*;
import com.google.gson.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
/** Identical harness also runs against exact 0.14.1 source, not a reimplemented baseline. */
class GeometryCapacityBenchmarkTest {
 static volatile Object blackhole;static MockedStatic<net.minecraftforge.fml.ModLoader> loader;
 @BeforeAll static void bootstrap(){loader=mockStatic(net.minecraftforge.fml.ModLoader.class);loader.when(net.minecraftforge.fml.ModLoader::get).thenReturn(mock(net.minecraftforge.fml.ModLoader.class));SharedConstants.tryDetectVersion();Bootstrap.bootStrap();}
 @AfterAll static void close(){loader.close();}
 static JsonArray circle(int count,double x,double z,double radius){var out=new JsonArray();for(int i=0;i<count;i++){double angle=i*2*Math.PI/count;var p=new JsonArray();p.add(x+radius*Math.cos(angle));p.add(z+radius*Math.sin(angle));out.add(p);}return out;}
 static String fixture(int holes,int outerPoints){var root=new JsonObject();root.addProperty("schema_version",2);root.addProperty("id","benchmark");root.addProperty("label","Synthetic Benchmark");root.addProperty("feature_type","sea");root.addProperty("dimension","minecraft:overworld");var geometry=new JsonObject();geometry.addProperty("type","polygon");geometry.add("outer",circle(outerPoints,0,0,5000));var inner=new JsonArray();for(int i=0;i<holes;i++)inner.add(circle(32,i%23*30,i/23*30,5));geometry.add("holes",inner);root.add("geometry",geometry);return root.toString();}
 static double medianMillis(Runnable task){for(int i=0;i<2;i++)task.run();var samples=new double[7];for(int i=0;i<samples.length;i++){long start=System.nanoTime();task.run();samples[i]=(System.nanoTime()-start)/1000000.0;}Arrays.sort(samples);return samples[samples.length/2];}
 @Test @Timeout(120) void benchmarkExactBaselineAndExpandedCapacity()throws Exception{
  var report=new LinkedHashMap<String,Object>();report.put("java",System.getProperty("java.version"));report.put("max_rings",FeatureGeometry.MAX_RINGS);report.put("max_vertices",FeatureGeometry.MAX_VERTICES);report.put("method","same runner / identical harness / 2 warmups + median of 7 / lookup median batch of 2000; no gameplay");var cases=new LinkedHashMap<String,Object>();
  for(var spec:List.of(new int[]{100,512},new int[]{156,512},new int[]{250,1024},new int[]{511,32})){
   int holes=spec[0],vertices=holes*32+spec[1];String name=holes==100?"shared_baseline":holes==156?"mediterranean_like":holes==250?"baltic_like":"maximum";var row=new LinkedHashMap<String,Object>();cases.put(name,row);row.put("holes",holes);row.put("rings",holes+1);row.put("vertices",vertices);
   if(holes+1>FeatureGeometry.MAX_RINGS||vertices>FeatureGeometry.MAX_VERTICES){row.put("supported",false);row.put("reason","exact baseline per-feature limit; no comparable successful pipeline exists");continue;}
   String json=fixture(holes,spec[1]);row.put("supported",true);row.put("json_chars",json.length());var feature=MapFeature.parse(JsonParser.parseString(json)).view();var index=new FeatureIndex(List.of(feature));var header=new FeaturePackets.Header(UUID.randomUUID(),1,1,1);
   row.put("validation_ms",medianMillis(()->blackhole=MapFeature.parse(JsonParser.parseString(json))));row.put("index_ms",medianMillis(()->blackhole=new FeatureIndex(List.of(feature))));row.put("serialization_ms",medianMillis(()->blackhole=FeaturePackets.frames(header,FeaturePackets.Mode.FULL,0,List.of(feature),List.of())));row.put("lookup_us",medianMillis(()->{for(int i=0;i<2000;i++)blackhole=index.hover("minecraft:overworld",i%2==0?-1000:0,0,2);})*1000/2000);
   var frames=FeaturePackets.frames(header,FeaturePackets.Mode.FULL,0,List.of(feature),List.of());int bytes=frames.stream().flatMap(f->f.fragments().stream()).mapToInt(f->f.data().length).sum();row.put("serialized_bytes",bytes);row.put("fragments",frames.size());assertEquals(vertices,feature.geometry().vertexCount());assertNotNull(index.hover("minecraft:overworld",-1000,0,2).feature());assertNull(index.hover("minecraft:overworld",0,0,2).feature());
  }
  report.put("cases",cases);var path=Path.of(System.getProperty("humanjourney.projectDir"),"build/reports/map-feature-capacity-benchmark.json");Files.createDirectories(path.getParent());Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(report));
 }
}
