package org.humanjourney.core.juice;
import net.minecraft.world.item.ItemStack;
import org.humanjourney.core.water.*;
public final class JuiceContainers {
 public static WaterContainers.Kind carrier(ItemStack stack){var copy=stack.copy();JuiceState.clear(copy);return WaterContainers.kind(copy);}
 public static boolean validCarrier(ItemStack stack){var k=carrier(stack);return k!=null&&k.canteen()&&k.quality()==WaterPolicy.Quality.PURIFIED&&!SaltWater.marked(stack)&&stack.getCount()==1&&stack.isDamageableItem()&&stack.getDamageValue()>=0&&stack.getDamageValue()<stack.getMaxDamage();}
 public static boolean fullPurified(ItemStack stack){return !JuiceState.marked(stack)&&validCarrier(stack)&&stack.getDamageValue()==0;}
 public static JuiceDefinition definition(ItemStack stack){return JuiceState.marked(stack)&&validCarrier(stack)?JuiceStore.state().catalog().definitions().get(JuiceState.id(stack)):null;}
 public static ItemStack create(ItemStack input,JuiceDefinition definition){if(!fullPurified(input))return ItemStack.EMPTY;var result=input.copy();JuiceState.set(result,definition);return result;}
 public static ItemStack consume(ItemStack stack,boolean creative){if(!validCarrier(stack))return stack;if(creative)return stack;if(stack.getDamageValue()+1<stack.getMaxDamage()){stack.setDamageValue(stack.getDamageValue()+1);return stack;}var empty=stack.copy();JuiceState.clear(empty);return WaterContainers.emptied(empty);}
 public static String describe(ItemStack stack){var kind=carrier(stack);return "juice="+JuiceState.id(stack)+" | tier="+(kind==null?"invalid":kind.tier())+" | remaining="+(stack.getMaxDamage()-stack.getDamageValue())+"/"+stack.getMaxDamage()+" | definition="+(definition(stack)==null?"UNRESOLVED/INVALID":"valid");}
 private JuiceContainers(){}
}
