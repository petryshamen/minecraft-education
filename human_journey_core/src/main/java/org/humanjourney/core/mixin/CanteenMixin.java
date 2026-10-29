package org.humanjourney.core.mixin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import org.humanjourney.core.water.WaterContainers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toughasnails.item.FilledCanteenItem;
import org.humanjourney.core.water.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/** Prevent TAN's enchanted-stack auto-replacement from bypassing treatment and dropping custom components. */
@Mixin(value=FilledCanteenItem.class,remap=false)
public class CanteenMixin {
    @Inject(method="finishUsingItem",at=@At("HEAD"),cancellable=true)
    private void hjSaltDrink(ItemStack stack,net.minecraft.world.level.Level level,LivingEntity entity,CallbackInfoReturnable<ItemStack> cir){
        var kind=WaterContainers.kind(stack);
        if(kind==null||kind.quality()!=WaterPolicy.Quality.SALT||!(entity instanceof net.minecraft.world.entity.player.Player player))return;
        player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(stack.getItem()));
        if(level.isClientSide()||player.getAbilities().instabuild){cir.setReturnValue(stack);return;}
        if(stack.getDamageValue()+1>=stack.getMaxDamage())cir.setReturnValue(WaterContainers.emptied(stack));
        else{stack.setDamageValue(stack.getDamageValue()+1);cir.setReturnValue(stack);}
    }
    @Inject(method="inventoryTick",at=@At("HEAD"),cancellable=true)
    private void hjPreserve(ItemStack stack,ServerLevel level,Entity entity,EquipmentSlot slot,CallbackInfo ci){if(WaterContainers.kind(stack)!=null)ci.cancel();}
}
