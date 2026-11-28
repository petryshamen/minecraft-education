package org.humanjourney.core.wrap.mixin;
import java.util.concurrent.*;
import net.minecraft.server.level.*;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.*;
import org.humanjourney.core.wrap.safety.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(GenerationChunkHolder.class)
public abstract class EarthHolderMixin implements EarthHolder {
 @Shadow public abstract ChunkPos getPos();
 @Shadow public abstract ChunkStatus getPersistedStatus();
 @Shadow private void failAndClearPendingFuturesBetween(ChunkStatus low,ChunkStatus high){throw new AssertionError();}
 public void hjEarth$fail(){failAndClearPendingFuturesBetween(null,ChunkStatus.FULL);}
 @Invoker("applyStep") public abstract CompletableFuture<ChunkResult<ChunkAccess>> hjEarth$apply(ChunkStep step,GeneratingChunkMap map,StaticCache2D<GenerationChunkHolder> cache);
 @Inject(method="applyStep",at=@At("HEAD"),cancellable=true)
 private void hjEarth$existingOnly(ChunkStep step,GeneratingChunkMap raw,StaticCache2D<GenerationChunkHolder> cache,CallbackInfoReturnable<CompletableFuture<ChunkResult<ChunkAccess>>> ci){
  if(!(raw instanceof EarthAccess map)||!EarthProtection.active(map.hjEarth$level()))return;
  if(!SafetyPolicy.chunkInside(getPos().x,getPos().z)){ci.setReturnValue(EarthProtection.reject(this));return;}
  if(step.targetStatus()==ChunkStatus.EMPTY){
   var proof=EarthProtection.existing(map,getPos());
   if(!proof.isDone()){ci.setReturnValue(proof.thenComposeAsync(ok->ok?hjEarth$apply(step,raw,cache):EarthProtection.reject(this),map.hjEarth$executor()));return;}
   if(!proof.getNow(false)){ci.setReturnValue(EarthProtection.reject(this));return;}
  }else{
   var persisted=getPersistedStatus();
   if(persisted==null||persisted.isBefore(step.targetStatus()))ci.setReturnValue(EarthProtection.reject(this));
  }
 }
}
