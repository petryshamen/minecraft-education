package org.humanjourney.core.mapfeature;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import net.minecraft.server.packs.resources.*;
import net.minecraft.resources.ResourceLocation;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
class FeatureReloadTest {
 @AfterEach void reset(){FeatureCatalog.reset();}
 void reload(String... json)throws Exception{var manager=mock(ResourceManager.class);var resources=new HashMap<ResourceLocation,Resource>();for(int i=0;i<json.length;i++){var r=mock(Resource.class);final String text=json[i];when(r.openAsReader()).thenAnswer(a->new BufferedReader(new StringReader(text)));when(r.sourcePackId()).thenReturn("synthetic");resources.put(ResourceLocation.parse("test:core/map_feature/file_"+i+".json"),r);}when(manager.listResources(eq("core/map_feature"),any())).thenReturn(resources);var barrier=new PreparableReloadListener.PreparationBarrier(){public <T> CompletableFuture<T> wait(T value){return CompletableFuture.completedFuture(value);}};new FeatureReloadListener().reload(barrier,manager,Runnable::run,Runnable::run).join();}
 @Test void realReloadListenerAcceptsThenRejectsWholeInvalidSet()throws Exception{reload(FeatureModelTest.polygon("old",1));var before=FeatureCatalog.state();reload(FeatureModelTest.line("new",1),"{}");assertSame(before.data(),FeatureCatalog.state().data());assertEquals(before.revision(),FeatureCatalog.state().revision());assertFalse(FeatureCatalog.state().error().isEmpty());}
 @Test void validEmptyReloadRemovesDefinitionsWithoutTouchingKnownIds()throws Exception{var known=new FeatureDiscovery(Set.of("known"),5);reload(FeatureModelTest.polygon("known",1));assertEquals(1,FeatureCatalog.project(FeatureCatalog.state().data(),known.snapshot().ids()).size());reload();assertTrue(FeatureCatalog.state().data().features().isEmpty());assertEquals(Set.of("known"),known.snapshot().ids());reload(FeatureModelTest.polygon("known",2));assertEquals(1,FeatureCatalog.project(FeatureCatalog.state().data(),known.snapshot().ids()).size());}
 @Test void commonFeatureBytecodeDoesNotLinkMinecraftClientXaeroOrNpcEconomy()throws Exception{for(String name:List.of("FeatureGeometry","MapFeature","FeatureIndex","FeatureCatalog","FeatureReloadListener","FeatureDiscovery","SavedFeatures","FeaturePackets","ClientFeatureState","FeatureNetwork","FeatureServer","FeatureCommands","FeatureBootstrap","FeatureSmoke")){try(var stream=getClass().getClassLoader().getResourceAsStream("org/humanjourney/core/mapfeature/"+name+".class")){assertNotNull(stream);var node=new ClassNode();new ClassReader(stream).accept(node,0);for(var f:node.fields)check(f.desc);for(var m:node.methods){check(m.desc);for(var i:m.instructions){if(i instanceof MethodInsnNode n){check(n.owner);check(n.desc);}if(i instanceof FieldInsnNode n){check(n.owner);check(n.desc);}if(i instanceof TypeInsnNode n)check(n.desc);}}}}}
 void check(String type){for(String forbidden:List.of("xaero/map/","net/minecraft/client/","core/npc/economy/","core/npc/context/"))assertFalse(type.contains(forbidden),type);}
}
