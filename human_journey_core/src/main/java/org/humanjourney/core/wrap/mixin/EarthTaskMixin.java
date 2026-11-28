package org.humanjourney.core.wrap.mixin;
import net.minecraft.server.level.*;
import org.humanjourney.core.wrap.safety.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** A failed dependency must also finish the centre request, not leave FULL pending. */
@Mixin(ChunkGenerationTask.class)
public abstract class EarthTaskMixin implements BoundaryHooks.Cancellation {
 @Shadow @Final private GeneratingChunkMap chunkMap;
 @Shadow public abstract GenerationChunkHolder getCenter();
 @Inject(method="waitForScheduledLayer",at=@At(value="INVOKE",target="Lnet/minecraft/server/level/ChunkGenerationTask;markForCancellation()V"))
 private void hjEarth$finishRejectedDependency(org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<java.util.concurrent.CompletableFuture<?>> ci){if(chunkMap instanceof EarthAccess map&&EarthProtection.active(map.hjEarth$level()))((EarthHolder)getCenter()).hjEarth$fail();}
}
