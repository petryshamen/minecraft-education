package org.humanjourney.core.geography;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.nio.file.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.*;
import net.minecraft.core.HolderLookup;
import org.humanjourney.core.npc.context.*;
class GeographyV2ReloadTest {
 @AfterEach void reset(){NpcContextStore.reset();}
 Resource resource(String text)throws Exception{var r=mock(Resource.class);when(r.openAsReader()).thenAnswer(a->new BufferedReader(new StringReader(text)));when(r.sourcePackId()).thenReturn("synthetic");return r;}
 void reload(String text)throws Exception{var manager=mock(ResourceManager.class);String config=Files.readString(Path.of(System.getProperty("humanjourney.projectDir"),"example-npc-context-datapack/data/human_journey/core/npc_context.json"));var configResource=resource(config);var geometryResource=resource(text);when(manager.getResource(ResourceLocation.parse("human_journey:core/npc_context.json"))).thenReturn(Optional.of(configResource));when(manager.listResources(eq("npc_geography"),any())).thenReturn(Map.of(ResourceLocation.parse("test:npc_geography/islands.json"),geometryResource));var barrier=new PreparableReloadListener.PreparationBarrier(){public <T> CompletableFuture<T> wait(T value){return CompletableFuture.completedFuture(value);}};new NpcContextReloadListener(mock(HolderLookup.Provider.class)).reload(barrier,manager,Runnable::run,Runnable::run).join();}
 @Test void realContextReloadPublishesGeometryAndIndexTogetherThenRejectsInvalidWholeSet()throws Exception{reload(GeographyV2Test.resource(2,"known",true,GeographyV2Test.geometry(2)).toString());var state=NpcContextStore.state();assertEquals("known",state.data().geography().resolve(GeographyV2Test.DIM,300,0,"era_0").local());var bad=GeographyV2Test.resource(2,"broken",true,JsonParser.parseString("{\"type\":\"polygon\",\"outer\":[[0,0],[100,100],[0,100],[100,0]]}").getAsJsonObject());reload(bad.toString());assertSame(state.data(),NpcContextStore.state().data());assertEquals(state.revision(),NpcContextStore.state().revision());assertTrue(NpcContextStore.state().error().contains("test:islands"));assertTrue(NpcContextStore.state().error().contains("broken"));assertEquals("known",NpcContextStore.state().data().geography().resolve(GeographyV2Test.DIM,300,0,"era_0").local());}
 @Test void oversizedGeographyResourceKeepsOldIndexAndReportsBound()throws Exception{reload(GeographyV2Test.resource(1,"legacy",true,null).toString());var state=NpcContextStore.state();reload(" ".repeat(524289));assertSame(state.data(),NpcContextStore.state().data());assertTrue(NpcContextStore.state().error().contains("512KiB"));}
}
