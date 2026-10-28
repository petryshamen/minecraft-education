package org.humanjourney.core.mixin;
import glitchcore.event.entity.LivingEntityUseItemEvent;
import net.minecraft.server.level.ServerPlayer;
import org.humanjourney.core.water.WaterDrinking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toughasnails.thirst.ThirstHandler;
@Mixin(value=ThirstHandler.class,remap=false)
public class ThirstHandlerMixin {
    @Inject(method="onItemUseFinish",at=@At("HEAD"),cancellable=true)
    private static void hjDrink(LivingEntityUseItemEvent.Finish event,CallbackInfo ci){
        if(event.getEntity() instanceof ServerPlayer player && WaterDrinking.item(player,event.getItem()))ci.cancel();
    }
}
