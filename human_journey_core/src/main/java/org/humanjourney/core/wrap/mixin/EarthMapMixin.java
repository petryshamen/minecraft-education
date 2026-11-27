package org.humanjourney.core.wrap.mixin;
import java.util.*;
import java.util.concurrent.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.humanjourney.core.wrap.safety.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(ChunkMap.class)
public abstract class EarthMapMixin implements EarthAccess {
 @Shadow @Final private ServerLevel level;
 @Shadow @Final private net.minecraft.util.thread.BlockableEventLoop<Runnable> mainThreadExecutor;
 @Shadow private CompletableFuture<Optional<CompoundTag>> readChunk(ChunkPos pos){throw new AssertionError();}
 public ServerLevel hjEarth$level(){return level;}
 public java.util.concurrent.Executor hjEarth$executor(){return mainThreadExecutor;}
 public CompletableFuture<Optional<CompoundTag>> hjEarth$read(ChunkPos pos){return readChunk(pos);}
 @Inject(method="save",at=@At("HEAD"),cancellable=true)
 private void hjEarth$noPlaceholderWrites(ChunkAccess chunk,CallbackInfoReturnable<Boolean> ci){if(EarthProtection.active(level)&&chunk.getPersistedStatus()!=ChunkStatus.FULL){EarthProtection.denied.incrementAndGet();ci.setReturnValue(false);}}
}
