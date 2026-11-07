package org.humanjourney.core.npc.economy.mixin;
import de.markusbordihn.easynpc.entity.easynpc.data.TradingDataCapable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import org.humanjourney.core.npc.economy.FamilyCosts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(value=TradingDataCapable.class,remap=false)
public interface TradingDataMixin {
 @Redirect(method="sanitizeTradingOffers",at=@At(value="INVOKE",target="Lde/markusbordihn/easynpc/entity/easynpc/data/TradingDataCapable;getItemCost(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/trading/ItemCost;"),require=2,expect=2,allow=2)
 private static ItemCost hjPreserve(ItemStack stack){return FamilyCosts.preserve(stack);}
}
