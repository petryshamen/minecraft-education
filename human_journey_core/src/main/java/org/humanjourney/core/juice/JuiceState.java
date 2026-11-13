package org.humanjourney.core.juice;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
/** Native CUSTOM_DATA persists and syncs without a juice/tier item or component registration. */
public final class JuiceState {
 public static final String KEY="human_journey:juice";
 public static boolean marked(ItemStack stack){var data=stack.get(DataComponents.CUSTOM_DATA);return data!=null&&data.copyTag().contains(KEY);}
 private static CompoundTag data(ItemStack stack){var d=stack.get(DataComponents.CUSTOM_DATA);return d==null?new CompoundTag():d.copyTag().getCompound(KEY).orElseGet(CompoundTag::new);}
 public static String id(ItemStack stack){return data(stack).getString("id").orElse("");}
 public static String display(ItemStack stack){String name=data(stack).getString("display_name").orElse("");return name.isBlank()||name.length()>128?(JuiceBottles.carrier(stack)?"Unresolved Juice Bottle":"Unresolved Juice Canteen"):name;}
 public static void set(ItemStack stack,JuiceDefinition definition){setNamed(stack,definition.id(),definition.canteenDisplayName());}
 public static void setBottle(ItemStack stack,JuiceDefinition definition){setNamed(stack,definition.id(),definition.bottle().displayName());}
 private static void setNamed(ItemStack stack,String id,String name){var old=stack.get(DataComponents.CUSTOM_DATA);var data=old==null?new CompoundTag():old.copyTag();var juice=new CompoundTag();juice.putString("id",id);juice.putString("display_name",name);data.put(KEY,juice);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));}
 public static void refreshLabel(ItemStack stack){if(!marked(stack))return;boolean bottle=JuiceBottles.carrier(stack);var def=bottle?JuiceBottles.definition(stack):JuiceContainers.definition(stack);if(def!=null){String name=bottle?def.bottle().displayName():def.canteenDisplayName();if(!display(stack).equals(name)){if(bottle)setBottle(stack,def);else set(stack,def);}}}
 public static void clear(ItemStack stack){var old=stack.get(DataComponents.CUSTOM_DATA);if(old==null)return;var data=old.copyTag();data.remove(KEY);if(data.isEmpty())stack.remove(DataComponents.CUSTOM_DATA);else stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));}
 private JuiceState(){}
}
