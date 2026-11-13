package org.humanjourney.core.juice;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
/** One carrier for every custom flavour. Reuses native TAN bottle completion, remainder and sound path. */
public final class CustomJuiceBottleItem extends toughasnails.item.DrinkItem {
 public CustomJuiceBottleItem(Properties properties){super(properties);}
 @Override public boolean canAlwaysDrink(){return false;} // Per-stack eligibility below uses the current effect source.
 @Override public InteractionResult use(Level level,Player player,InteractionHand hand){var s=player.getItemInHand(hand);if(level.isClientSide())return JuiceState.marked(s)?ItemUtils.startUsingInstantly(level,player,hand):InteractionResult.FAIL;var d=JuiceBottles.definition(s);if(d==null){if(player instanceof ServerPlayer p)JuiceDrinking.invalid(p,s);return InteractionResult.FAIL;}var source=(toughasnails.item.JuiceItem)BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(d.effectSourceItem()));return toughasnails.api.thirst.ThirstHelper.canDrink(player,source.canAlwaysDrink())?ItemUtils.startUsingInstantly(level,player,hand):InteractionResult.FAIL;}
 @Override public ItemStack finishUsingItem(ItemStack stack,Level level,LivingEntity entity){return JuiceBottles.finish(stack,level,entity,s->super.finishUsingItem(s,level,entity));}
 @Override public void inventoryTick(ItemStack stack,ServerLevel level,Entity entity,EquipmentSlot slot){JuiceState.refreshLabel(stack);}
}
