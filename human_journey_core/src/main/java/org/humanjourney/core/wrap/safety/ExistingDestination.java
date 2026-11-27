package org.humanjourney.core.wrap.safety;
import java.util.*;
import java.util.concurrent.*;
import net.minecraft.server.level.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.*;
import net.minecraft.world.phys.AABB;

/** Tick-thread jobs, shared tickets reference-counted by level/position. No FULL generation requests. */
public final class ExistingDestination implements AutoCloseable {
 private record Key(ServerLevel level,ChunkPos pos){}
 private static final Map<Key,Integer> TICKETS=new HashMap<>();
 private static final net.minecraftforge.registries.DeferredRegister<TicketType> TYPES=net.minecraftforge.registries.DeferredRegister.create(net.minecraft.core.registries.Registries.TICKET_TYPE,"human_journey_core");
 private static final net.minecraftforge.registries.RegistryObject<TicketType> TYPE=TYPES.register("earth_wrap",()->new TicketType(241,false,TicketType.TicketUse.LOADING));
 public static void register(net.minecraftforge.eventbus.api.bus.BusGroup group){TYPES.register(group);}
 private final ServerLevel level;private final List<ChunkPos> targets;private final CompletableFuture<Boolean> proof;private boolean held,closed;
 public String failure="";
 public ExistingDestination(ServerLevel level,List<AABB> boxes){
  this.level=level;var required=new TreeSet<ChunkPos>(Comparator.comparingInt((ChunkPos p)->p.x).thenComparingInt(p->p.z));var core=new HashSet<ChunkPos>();
  int dependency=ChunkPyramid.LOADING_PYRAMID.getStepTo(ChunkStatus.FULL).accumulatedDependencies().getRadius();
  for(var box:boxes){for(int x=((int)Math.floor(box.minX-1))>>4;x<=(((int)Math.floor(box.maxX+1))>>4);x++)for(int z=((int)Math.floor(box.minZ-1))>>4;z<=(((int)Math.floor(box.maxZ+1))>>4);z++){
   core.add(new ChunkPos(x,z));for(int a=x-dependency;a<=x+dependency;a++)for(int b=z-dependency;b<=z+dependency;b++)required.add(new ChunkPos(a,b));
  }}
  targets=List.copyOf(core);
  if(required.size()>81||targets.size()>25){failure="destination exceeds bounded 81 dependency / 25 target chunk budget";proof=CompletableFuture.completedFuture(false);return;}
  if(required.stream().anyMatch(p->!SafetyPolicy.chunkInside(p.x,p.z))){failure="destination footprint/loading dependency reaches outside canonical Earth; use explicit inward-arrival setting or remain held";proof=CompletableFuture.completedFuture(false);return;}
  if(!(level.getChunkSource().chunkMap instanceof EarthAccess map)){failure="existing-only storage bridge missing";proof=CompletableFuture.completedFuture(false);return;}
  CompletableFuture<Boolean> chain=CompletableFuture.completedFuture(true);
  for(var pos:required)chain=chain.thenCompose(ok->ok?EarthProtection.existing(map,pos):CompletableFuture.completedFuture(false));
  proof=chain;
 }
 public boolean ready(){
  if(closed)return false;
  if(!proof.isDone())return false;
  if(!proof.getNow(false)){if(failure.isEmpty())failure="missing/incomplete/corrupt existing destination or storage budget unavailable";return false;}
  if(!held){for(var pos:targets){var key=new Key(level,pos);int n=TICKETS.getOrDefault(key,0);if(n==0)level.getChunkSource().addTicketWithRadius(TYPE.get(),pos,0);TICKETS.put(key,n+1);}held=true;}
  return targets.stream().allMatch(p->level.getChunkSource().getChunkNow(p.x,p.z)!=null);
 }
 public boolean failed(){return proof.isDone()&&!proof.getNow(false);}
 public void close(){if(closed)return;closed=true;if(held)for(var pos:targets){var key=new Key(level,pos);int n=TICKETS.getOrDefault(key,0);if(n<=1){TICKETS.remove(key);level.getChunkSource().removeTicketWithRadius(TYPE.get(),pos,0);}else TICKETS.put(key,n-1);}}
 public static int ticketCount(){return TICKETS.size();}
 public static void clear(){for(var key:List.copyOf(TICKETS.keySet()))key.level.getChunkSource().removeTicketWithRadius(TYPE.get(),key.pos,0);TICKETS.clear();}
}
