package org.humanjourney.core.wrap.safety;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.chunk.status.*;
import org.humanjourney.core.wrap.*;

/** Protection is existing-FULL-only for the opted-in Overworld, including interior holes. */
public final class EarthProtection {
 private static volatile MinecraftServer server;
 private static volatile boolean active,ready;
 private static final Map<EarthAccess,Map<Long,CompletableFuture<Boolean>>> READS=new WeakHashMap<>();
 private static volatile int inFlight;private static long epoch;
 private static final ArrayDeque<Runnable> WAITING=new ArrayDeque<>();
 public static final AtomicLong denied=new AtomicLong();
 public static boolean active(ServerLevel level){return active&&level.dimension()==Level.OVERWORLD&&level.getServer()==server;}
 public static boolean ready(){return active&&ready;}
 public static void start(MinecraftServer current){
  clear();server=current;active=WrapConfig.PROTECTION.get();
  if(!active){if(WrapConfig.ENABLED.get())throw new IllegalStateException("Physical wrapping requested without Earth protection: refusing startup. Disable wrapping or explicitly enable tested protection.");return;}
  if(!WrapMixinPlugin.supported()||!EarthAccess.class.isAssignableFrom(ChunkMap.class)||!EarthHolder.class.isAssignableFrom(GenerationChunkHolder.class)||!WrapConnection.class.isAssignableFrom(net.minecraft.server.network.ServerGamePacketListenerImpl.class)||!BoundaryHooks.LevelReads.class.isAssignableFrom(Level.class)||!BoundaryHooks.Movement.class.isAssignableFrom(net.minecraft.world.entity.Entity.class)||!BoundaryHooks.Packets.class.isAssignableFrom(net.minecraft.server.network.ServerGamePacketListenerImpl.class)||!BoundaryHooks.Cancellation.class.isAssignableFrom(ChunkGenerationTask.class))throw new IllegalStateException("Earth protection requested but exact safety hooks are unavailable; refusing server startup");
  if(ChunkPyramid.LOADING_PYRAMID.getStepTo(ChunkStatus.FULL).accumulatedDependencies().getRadius()*16>SafetyPolicy.BUFFER)throw new IllegalStateException("Native loading dependencies exceed validated boundary buffer");
  // Activation precedes level loading. A blank world is an invalid existing-only Earth.
  var region=current.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).resolve("region");
  try(var files=java.nio.file.Files.list(region)){if(files.noneMatch(p->p.getFileName().toString().endsWith(".mca")))throw new IllegalStateException("Earth protection requires an existing imported world; no region files found");}
  catch(java.io.IOException e){throw new IllegalStateException("Cannot verify existing Earth world",e);}
  ready=true;
 }
 public static void clear(){synchronized(READS){active=false;ready=false;server=null;epoch++;for(var cache:READS.values())for(var f:cache.values())f.complete(false);READS.clear();WAITING.clear();inFlight=0;denied.set(0);}}
 public static CompletableFuture<Boolean> existing(EarthAccess map,ChunkPos pos){
  if(!SafetyPolicy.chunkInside(pos.x,pos.z)){denied.incrementAndGet();return CompletableFuture.completedFuture(false);}
  synchronized(READS){
   var cache=READS.computeIfAbsent(map,k->new LinkedHashMap<>());var prior=cache.get(pos.toLong());if(prior!=null)return prior;
   if(WAITING.size()>=1024)return CompletableFuture.completedFuture(false);
   if(cache.size()>=4096){var it=cache.entrySet().iterator();while(it.hasNext()&&cache.size()>=4096){if(it.next().getValue().isDone())it.remove();}if(cache.size()>=4096)return CompletableFuture.completedFuture(false);}
   long session=epoch;var result=new CompletableFuture<Boolean>();cache.put(pos.toLong(),result);
   Runnable read=()->{inFlight++;try{map.hjEarth$read(pos).orTimeout(10,TimeUnit.SECONDS).whenComplete((tag,error)->{
    boolean valid=error==null&&tag!=null&&tag.isPresent()&&full(tag.get(),pos);
    synchronized(READS){if(epoch!=session){result.complete(false);return;}inFlight--;if(!valid)cache.remove(pos.toLong(),result);}
    if(!valid)denied.incrementAndGet();result.complete(valid);drain();
   });}catch(RuntimeException failure){inFlight--;cache.remove(pos.toLong());result.complete(false);drain();}};
   WAITING.add(read);drain();
   return result;
  }
 }
 private static void drain(){synchronized(READS){while(inFlight<32&&!WAITING.isEmpty())WAITING.remove().run();}}
 public static boolean full(CompoundTag tag,ChunkPos expected){return tag.getString("Status").orElse("").equals("minecraft:full")&&tag.getInt("xPos").orElse(Integer.MIN_VALUE)==expected.x&&tag.getInt("zPos").orElse(Integer.MIN_VALUE)==expected.z&&tag.getList("sections").isPresent();}
 public static CompletableFuture<ChunkResult<ChunkAccess>> reject(EarthHolder holder){holder.hjEarth$fail();denied.incrementAndGet();return CompletableFuture.completedFuture(ChunkResult.error("HJ Earth protection: existing FULL data required; generation prohibited"));}
 public static String describe(){return "protection="+active+" ready="+ready()+" existing-FULL-only=true denied="+denied.get()+" pending_disk_reads="+inFlight;}
 private EarthProtection(){}
}
