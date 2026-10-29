package org.humanjourney.core.mixin;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.humanjourney.core.water.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toughasnails.item.EmptyCanteenItem;
@Mixin(value=EmptyCanteenItem.class,remap=false)
public class CanteenFillMixin {
    @Inject(method="fillCanteen",at=@At("HEAD"),cancellable=true)
    private void hjNatural(Level level,Player player,ItemStack stack,CallbackInfoReturnable<InteractionResult> cir){
        var hand=player.getMainHandItem()==stack?InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND;
        var result=WaterFilling.natural(player,hand);if(result!=null)cir.setReturnValue(result);
    }
}
