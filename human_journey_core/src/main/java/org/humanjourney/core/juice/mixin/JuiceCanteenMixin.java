package org.humanjourney.core.juice.mixin;
import org.humanjourney.core.juice.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toughasnails.api.thirst.ThirstHelper;
@Mixin(value=toughasnails.item.FilledCanteenItem.class,remap=false,priority=1100)
public class JuiceCanteenMixin {
 @Inject(method="use",at=@At("HEAD"),cancellable=true)
 private void hjJuiceUse(Level level,Player player,InteractionHand hand,CallbackInfoReturnable<InteractionResult> ci){var stack=player.getItemInHand(hand);if(!JuiceState.marked(stack))return;if(level.isClientSide()){ci.setReturnValue(ItemUtils.startUsingInstantly(level,player,hand));return;}var def=JuiceContainers.definition(stack);if(def==null){if(player instanceof ServerPlayer server)JuiceDrinking.invalid(server,stack);ci.setReturnValue(InteractionResult.FAIL);return;}var bottle=(toughasnails.item.JuiceItem)net.minecraft.core.registries.BuiltInRegistries.ITEM.getValue(net.minecraft.resources.ResourceLocation.parse(def.effectSourceItem()));ci.setReturnValue(ThirstHelper.canDrink(player,bottle.canAlwaysDrink())?ItemUtils.startUsingInstantly(level,player,hand):InteractionResult.FAIL);}
 @Inject(method="finishUsingItem",at=@At("HEAD"),cancellable=true)
 private void hjJuiceFinish(ItemStack stack,Level level,LivingEntity entity,CallbackInfoReturnable<ItemStack> ci){if(JuiceState.marked(stack))ci.setReturnValue(JuiceDrinking.canteen(stack,level,entity));}
}
