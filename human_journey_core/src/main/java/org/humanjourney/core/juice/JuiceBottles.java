package org.humanjourney.core.juice;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
public final class JuiceBottles {
 public static boolean carrier(ItemStack s){return !s.isEmpty()&&BuiltInRegistries.ITEM.getKey(s.getItem()).equals(ResourceLocation.parse("human_journey_core:juice_bottle"));}
 public static boolean purified(ItemStack s){return !JuiceState.marked(s)&&!org.humanjourney.core.water.SaltWater.marked(s)&&!s.isEmpty()&&BuiltInRegistries.ITEM.getKey(s.getItem()).equals(ResourceLocation.parse("toughasnails:purified_water_bottle"));}
 public static JuiceDefinition definition(ItemStack s){if(!carrier(s)||!JuiceState.marked(s))return null;var d=JuiceStore.state().catalog().definitions().get(JuiceState.id(s));return d!=null&&d.bottle().custom()?d:null;}
 public static ItemStack create(ItemStack water,JuiceDefinition d){var item=BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(d.bottleItem()));if(item==Items.AIR)return ItemStack.EMPTY;var output=water.transmuteCopy(item,1);if(d.bottle().custom())JuiceState.setBottle(output,d);return output;}
 public static ItemStack finish(ItemStack stack,net.minecraft.world.level.Level level,net.minecraft.world.entity.LivingEntity entity,java.util.function.Function<ItemStack,ItemStack> nativeFinish){if(level.isClientSide()||!(entity instanceof net.minecraft.server.level.ServerPlayer player))return stack;var d=definition(stack);if(d==null){JuiceDrinking.invalid(player,stack);return stack;}NativeJuiceEffects.apply(player,new ItemStack(BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(d.effectSourceItem()))));JuiceDrinking.heal(player,d.healPerDrink());return nativeFinish.apply(stack);}
 public static String describe(ItemStack s){var d=definition(s);return "custom bottle | juice="+JuiceState.id(s)+" | name="+JuiceState.display(s)+" | definition="+(d==null?"UNRESOLVED/INVALID":"valid | effect_source="+d.effectSourceItem());}
 private JuiceBottles(){}
}
