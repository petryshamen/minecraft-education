package org.humanjourney.core.mixin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.humanjourney.core.water.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import toughasnails.block.entity.WaterPurifierBlockEntity;
@Mixin(value=WaterPurifierBlockEntity.class,remap=false)
public abstract class PurifierMixin implements PurifierAccess {
    @Shadow private int filterTimeRemaining;
    @Shadow private int filterDuration;
    @Shadow private int purifyProgress;
    @Shadow private int purifyTotalTime;
    public int hjRemaining(){return filterTimeRemaining;}
    public int hjDuration(){return filterDuration;}
    public void hjCapacity(int remaining,int duration){filterTimeRemaining=remaining;filterDuration=duration;}
    @Inject(method="serverTick",at=@At("HEAD"),cancellable=true)
    private static void hjInstant(Level level,BlockPos pos,BlockState state,WaterPurifierBlockEntity entity,CallbackInfo ci){WaterPurifier.tick(level,pos,entity);ci.cancel();}
    // Native setItem() must not query a ServerLevel recipe cache on client or for new empty-container inputs.
    @Inject(method="getTotalPurifyTime",at=@At("HEAD"),cancellable=true)
    private void hjNoWait(CallbackInfoReturnable<Integer> cir){cir.setReturnValue(1);}
}
