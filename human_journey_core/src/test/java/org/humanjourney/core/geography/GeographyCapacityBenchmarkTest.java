package org.humanjourney.core.geography;
import java.util.*;
import java.util.zip.*;
import java.nio.file.*;
import java.security.*;
import com.google.gson.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.humanjourney.core.npc.context.*;
import org.humanjourney.core.geography.map.*;
/** Identical harness also runs against exact0.15.0 with the same frozen external bytes. */
class GeographyCapacityBenchmarkTest {
 static volatile Object sink;static MockedStatic<net.minecraftforge.fml.ModLoader> loader;
 @BeforeAll static void bootstrap(){loader=mockStatic(net.minecraftforge.fml.ModLoader.class);loader.when(net.minecraftforge.fml.ModLoader::get).thenReturn(mock(net.minecraftforge.fml.ModLoader.class));SharedConstants.tryDetectVersion();Bootstrap.bootStrap();}
 @AfterAll static void close(){loader.close();}
 static Path project(){return Path.of(System.getProperty("humanjourney.projectDir"));}
 static final class Review {
  final Map<String,String> raw=new TreeMap<>();final List<String> refined=new ArrayList<>();final List<String> proposed=new ArrayList<>();final JsonObject manifest;
  Review()throws Exception{try(var zip=new ZipFile(project().resolve("src/test/resources/geography/europe-refinement-v1-fixture.zip").toFile())){manifest=JsonParser.parseString(new String(zip.getInputStream(zip.getEntry("FIXTURE_MANIFEST.json")).readAllBytes(),java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();for(var e:manifest.getAsJsonObject("files").entrySet()){var data=zip.getInputStream(zip.getEntry(e.getKey())).readAllBytes();assertEquals(e.getValue().getAsString(),HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(data)),e.getKey());if(e.getKey().endsWith(".json"))raw.put(e.getKey(),new String(data,java.nio.charset.StandardCharsets.UTF_8));}}var replace=new HashSet<String>();for(var n:JsonParser.parseString(raw.get("audit/migration.json")).getAsJsonObject().getAsJsonArray("replace_original_aggregates"))replace.add(n.getAsString());for(String path:raw.keySet()){if(path.startsWith("candidate/data/human_journey/npc_geography/europe_refined/"))refined.add(path);if(path.startsWith("candidate/data/human_journey/npc_geography/")||path.startsWith("baseline/")&&!replace.contains(path.substring("baseline/".length())))proposed.add(path);}assertEquals(190,refined.size());}
  List<JsonElement> json(List<String> paths){return paths.stream().map(p->JsonParser.parseString(raw.get(p))).toList();}
  JsonElement macros(){return JsonParser.parseString(raw.get("baseline/global_macros.json"));}
 }
 static long[] counts(JsonObject g){long v=0,r=0;var parts=g.get("type").getAsString().equals("polygon")?List.of(g):g.getAsJsonArray("polygons").asList();for(var e:parts){var p=e.getAsJsonObject();v+=p.getAsJsonArray("outer").size();r++;if(p.has("holes"))for(var h:p.getAsJsonArray("holes")){v+=h.getAsJsonArray().size();r++;}}return new long[]{v,r};}
 static JsonArray ring(int n,double x,double z,double radius){var a=new JsonArray();for(int i=0;i<n;i++){double angle=i*2*Math.PI/n;var p=new JsonArray();p.add(x+radius*Math.cos(angle));p.add(z+radius*Math.sin(angle));a.add(p);}return a;}
 static JsonObject fixture(int holes,int outer){var root=new JsonObject();root.addProperty("schema_version",2);var area=new JsonObject();area.addProperty("id","synthetic");area.addProperty("display_label","Synthetic");var g=new JsonObject();g.addProperty("type","polygon");g.add("outer",ring(outer,0,0,5000));var h=new JsonArray();for(int i=0;i<holes;i++)h.add(ring(32,i%23*30,i/23*30,5));g.add("holes",h);area.add("geometry",g);var a=new JsonArray();a.add(area);root.add("local_areas",a);return root;}
 static double median(Runnable task){for(int i=0;i<2;i++)task.run();double[] values=new double[5];for(int i=0;i<5;i++){long t=System.nanoTime();task.run();values[i]=(System.nanoTime()-t)/1e6;}Arrays.sort(values);return values[2];}
 @Test @Timeout(180) void compareLargeValidationIndexResolutionAndTransfer()throws Exception{
  var report=new LinkedHashMap<String,Object>();report.put("max_vertices",GeographyGeometry.MAX_VERTICES);report.put("max_rings",GeographyGeometry.MAX_RINGS);report.put("java",System.getProperty("java.version"));report.put("method","same runner/exact source/identical harness;2warmups,median5;lookup batches256;validation includes JSON/native parser/index;index constructor timing separately;transfer is raw serialization/fragment preparation,not network latency;no gameplay");var cases=new LinkedHashMap<String,Object>();var review=new Review();var fixtures=new LinkedHashMap<String,String>();fixtures.put("shared_100_holes",fixture(100,512).toString());fixtures.put("synthetic_250_holes",fixture(250,1024).toString());fixtures.put("synthetic_maximum",fixture(511,32).toString());for(String p:review.refined){var a=JsonParser.parseString(review.raw.get(p)).getAsJsonObject().getAsJsonArray("local_areas").get(0).getAsJsonObject();if(Set.of("bothnian_coast","scandinavian_mountains").contains(a.get("id").getAsString())){var standalone=JsonParser.parseString(review.raw.get(p)).getAsJsonObject();standalone.getAsJsonArray("local_areas").get(0).getAsJsonObject().remove("macro_region");fixtures.put(a.get("id").getAsString(),standalone.toString());}}
  for(var entry:fixtures.entrySet()){var root=JsonParser.parseString(entry.getValue()).getAsJsonObject();var area=root.getAsJsonArray("local_areas").get(0).getAsJsonObject();var n=counts(area.getAsJsonObject("geometry"));var row=new LinkedHashMap<String,Object>();cases.put(entry.getKey(),row);row.put("vertices",n[0]);row.put("rings",n[1]);if(n[0]>GeographyGeometry.MAX_VERTICES||n[1]>GeographyGeometry.MAX_RINGS){row.put("supported",false);row.put("reason","exact0.15.0 capacity");continue;}row.put("supported",true);String text=entry.getValue();var geo=NpcGeography.parse(List.of(root),Set.of("era_0","era_1"));var a=geo.locals().getFirst();var views=new MapProjection.Factory(geo,"era_0").project(new DiscoveryLedger.Snapshot(Set.of(),Set.of(a.id()),1)).areas().values();var header=new MapPackets.Header(UUID.randomUUID(),1,1,1,1);var p=a.shape().parts().getFirst().outer().points().getFirst();row.put("validate_parse_index_ms",median(()->sink=NpcGeography.parse(List.of(JsonParser.parseString(text)),Set.of("era_0","era_1"))));row.put("index_build_ms",geo.indexBuildNanos()/1e6);row.put("point_resolution_us",median(()->{for(int i=0;i<256;i++)sink=geo.resolve(a.dimension(),p.x(),p.z(),"era_0");})*1000/256);row.put("candidates",geo.candidateCount(a.dimension(),p.x(),p.z()));row.put("transfer_preparation_ms",median(()->sink=MapPackets.frames(header,MapPackets.Mode.FULL,0,views,List.of())));row.put("fragments",MapPackets.frames(header,MapPackets.Mode.FULL,0,views,List.of()).size());}
  report.put("cases",cases);var target=project().resolve("build/reports/geography-capacity-benchmark.json");Files.createDirectories(target.getParent());Files.writeString(target,new GsonBuilder().setPrettyPrinting().create().toJson(report));
 }
}
