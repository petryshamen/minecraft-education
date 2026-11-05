package org.humanjourney.core.fishing;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
/** Exact 1.21.8 catchingFish: nibble draw, approach draw, then initial wait draw (ordinal 2). */
@Mixin(value=FishingHook.class,remap=false)
public abstract class FishingHookMixin {
    @Shadow @Final private int lureSpeed;
    @Redirect(method="catchingFish(Lnet/minecraft/core/BlockPos;)V",at=@At(value="INVOKE",target="Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I",ordinal=2),require=1,expect=1,allow=1)
    private int hjcore$initialWait(RandomSource random,int minimum,int maximum){return FishingTiming.initialDraw(random,minimum,maximum,lureSpeed);}
}
