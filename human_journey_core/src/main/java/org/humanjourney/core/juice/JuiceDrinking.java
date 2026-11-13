package org.humanjourney.core.juice;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
public final class JuiceDrinking {
 public static float cappedHeal(float health,float max,double requested){return (float)Math.max(0,Math.min(requested,(double)max-health));}
 public static void heal(ServerPlayer player,double value){float amount=cappedHeal(player.getHealth(),player.getMaxHealth(),value);if(amount>0)player.heal(amount);}
 public static void bottle(Item item,Level level,LivingEntity entity){if(level.isClientSide()||!(entity instanceof ServerPlayer player))return;var definition=JuiceStore.state().catalog().bottles().get(BuiltInRegistries.ITEM.getKey(item).toString());if(definition!=null)heal(player,definition.healPerDrink());}
 public static void invalid(ServerPlayer player,ItemStack stack){player.displayClientMessage(Component.literal("Cannot drink: unknown or invalid juice canteen ("+JuiceState.id(stack)+"). Ask an administrator to inspect /hjcore juice."),true);}
 public static ItemStack canteen(ItemStack stack,Level level,LivingEntity entity){if(level.isClientSide()||!(entity instanceof ServerPlayer player))return stack;var def=JuiceContainers.definition(stack);if(def==null){invalid(player,stack);return stack;}var bottle=new ItemStack(BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(def.bottleItem())));NativeJuiceEffects.apply(player,bottle);heal(player,def.healPerDrink());player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(stack.getItem()));return JuiceContainers.consume(stack,player.getAbilities().instabuild);}
 private JuiceDrinking(){}
}
