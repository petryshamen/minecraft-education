package org.humanjourney.core.juice.mixin;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.humanjourney.core.juice.JuiceDrinking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/** Post-native completion only; no replacement of bottle consumption, effects, stats or remainder. */
@Mixin(value=toughasnails.item.DrinkItem.class,remap=false)
public class BottleJuiceMixin {
 @Inject(method="finishUsingItem",at=@At("RETURN"))
 private void hjJuiceHeal(ItemStack stack,Level level,LivingEntity entity,CallbackInfoReturnable<ItemStack> ci){JuiceDrinking.bottle((Item)(Object)this,level,entity);}
}
