package org.humanjourney.core.npc.economy.mixin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import org.humanjourney.core.npc.economy.FamilyCosts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=ItemCost.class,remap=false)
public abstract class ItemCostMixin {
 @Inject(method="test(Lnet/minecraft/world/item/ItemStack;)Z",at=@At("HEAD"),cancellable=true,require=1)
 private void hjPayment(ItemStack stack,CallbackInfoReturnable<Boolean> ci){var cost=(ItemCost)(Object)this;if(FamilyCosts.marked(cost.itemStack()))ci.setReturnValue(FamilyCosts.accepts(cost,stack));}
}
