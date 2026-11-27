package org.humanjourney.core.wrap;
import java.util.*;
import java.nio.file.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.*;
import org.humanjourney.core.mapfeature.*;
import org.humanjourney.core.geography.*;
import org.humanjourney.core.npc.context.NpcGeography;

class WrapCompatibilityTest {
 static final Path ROOT=Path.of(System.getProperty("humanjourney.projectDir","."));
 String source(String name)throws Exception{return Files.readString(ROOT.resolve("src/main/java/org/humanjourney/core/wrap/"+name+".java"));}
 @Test void disabledByDefault(){assertFalse(WrapConfig.ENABLED.getDefault());}
 @Test void activationIsOverworldOnlyAndNoNorthSouthWrap()throws Exception{var s=source("WrapRuntime");assertTrue(s.contains("player.level().dimension()!=Level.OVERWORLD"));assertTrue(s.contains("!WrapConfig.ENABLED.get()"));assertTrue(s.contains("Math.abs(root.getZ())>6144"));assertFalse(s.contains("Level.NETHER"));}
 @Test void noPersistentDuplicateLedgerOrEntityRecreation()throws Exception{String s=source("WrapRuntime");for(String token:List.of("addFreshEntity","EntityType.create","saveWithoutId","readAdditionalSaveData","startRiding","stopRiding","ejectPassengers","setUUID","SavedData","setHealth","setFoodLevel","setThirst","award(","revoke("))assertFalse(s.contains(token),token);assertTrue(s.contains("getRootVehicle()"));assertTrue(s.contains("IdentityHashMap"));}
 @Test void chunkSafetyNeverLoadsOrGenerates()throws Exception{String s=source("WrapRuntime");assertTrue(s.contains("getChunkNow("));assertFalse(s.contains("getChunk("));assertFalse(s.contains("getChunkFuture("));assertFalse(s.contains("addRegionTicket"));}
 @Test void acknowledgementGuardRunsAfterMainThreadDispatch()throws Exception{var s=source("mixin/WrapConnectionMixin");assertTrue(s.contains("ensureRunningOnSameThread"));assertTrue(s.contains("shift=At.Shift.AFTER"));assertTrue(s.contains("if(awaitingPositionFromClient!=null)ci.cancel()"));}
 @Test void successfulAcknowledgementRetiresWrapOnlyGuard()throws Exception{var s=source("mixin/WrapConnectionMixin");assertTrue(s.contains("method=\"handleAcceptTeleportPacket\",at=@At(\"RETURN\")"));assertTrue(s.contains("if(awaitingPositionFromClient==null)hjWrap$waiting=false"));}
 @Test void groupSizeAndWarningsAreBounded()throws Exception{var s=source("WrapRuntime");assertTrue(s.contains("entities.size()>=16"));assertTrue(s.contains("now-before>=200"));assertTrue(s.contains("new WeakHashMap"));}
 @Test void reconnectRestartNeedsNoIndependentWrapState()throws Exception{var s=source("WrapBootstrap");assertTrue(s.contains("ServerStoppedEvent"));assertTrue(s.contains("WrapRuntime.clear()"));assertFalse(source("WrapRuntime").contains("PlayerLoggedInEvent"));assertTrue(source("WrapRuntime").contains("!player.isAlive()||player.isSleeping()"));}
 @Test void noXaeroInjectionOrNewVisualPromise()throws Exception{var s=source("WrapRuntime")+source("WrapBootstrap");assertFalse(s.contains("import xaero"));assertTrue(s.contains("Xaero visual wrap: not implemented"));var config=JsonParser.parseString(Files.readString(ROOT.resolve("src/main/resources/human_journey_core.wrap.mixins.json"))).getAsJsonObject();assertFalse(config.has("client"));}
 @Test void seamFeatureRemainsOneDiscoveryAcrossCrossing()throws Exception{
  String json=Files.readString(ROOT.resolve("example-map-features-v2/data/human_journey/core/map_feature/synthetic_v2_seam.json"));
  var data=FeatureCatalog.prepare(List.of(new FeatureCatalog.Source("test:seam","synthetic",json)));
  var feature=data.features().get("synthetic_v2_seam");double x=WrapMath.canonical(12288.25);
  assertTrue(feature.view().geometry().contains(12287.75,50));assertTrue(feature.view().geometry().contains(x,50));
  var ledger=new FeatureDiscovery();var roles=new GeographyRoles(true,true,false);assertEquals(Set.of("synthetic_v2_seam"),ledger.discover(roles,data,"minecraft:overworld",12287.75,50));
  assertTrue(ledger.discover(roles,data,"minecraft:overworld",x,50).isEmpty());assertEquals(1,ledger.snapshot().ids().size());
  var reloaded=new FeatureDiscovery(ledger.snapshot().ids(),ledger.snapshot().revision());assertEquals(ledger.snapshot(),reloaded.snapshot());
 }
 @Test void indexedCanonicalGeographyResolvesAfterWrap()throws Exception{
  var fixture=JsonParser.parseString("{\"schema_version\":1,\"macro_regions\":[{\"id\":\"west_fixture\",\"dimension\":\"minecraft:overworld\",\"polygon\":[[-12288,0],[-12200,0],[-12200,100],[-12288,100]]}],\"local_areas\":[]}");
  var geo=NpcGeography.parse(List.of(fixture),Set.of("era_0"));
  assertEquals("none",geo.resolve("minecraft:overworld",12288.25,50,"era_0").macro());
  assertEquals("west_fixture",geo.resolve("minecraft:overworld",WrapMath.canonical(12288.25),50,"era_0").macro());
  assertEquals("none",geo.resolve("minecraft:the_nether",WrapMath.canonical(12288.25),50,"era_0").macro());
 }
}
