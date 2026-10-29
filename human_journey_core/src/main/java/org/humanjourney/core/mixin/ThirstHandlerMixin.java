package org.humanjourney.core.mixin;
import glitchcore.event.entity.LivingEntityUseItemEvent;
import net.minecraft.server.level.ServerPlayer;
import org.humanjourney.core.water.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toughasnails.thirst.ThirstHandler;
@Mixin(value=ThirstHandler.class,remap=false)

public class ThirstHandlerMixin {
    @Inject(method="onPlayerUseItem",at=@At("HEAD"),cancellable=true)
    private static void hjFill(glitchcore.event.player.PlayerInteractEvent.UseItem event,CallbackInfo ci){
        if(!event.getPlayer().getItemInHand(event.getHand()).is(net.minecraft.world.item.Items.GLASS_BOTTLE))return;
        var result=WaterFilling.natural(event.getPlayer(),event.getHand());
        if(result!=null){event.setCancelResult(result);event.setCancelled(true);ci.cancel();}
    }

    @Inject(method="onItemUseFinish",at=@At("HEAD"),cancellable=true)
    private static void hjDrink(LivingEntityUseItemEvent.Finish event,CallbackInfo ci){
        if(event.getEntity() instanceof ServerPlayer player && WaterDrinking.item(player,event.getItem()))ci.cancel();
    }
}
