package org.humanjourney.core.npc.economy;
import java.util.*;
import net.minecraft.core.component.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.trading.ItemCost;
/** Costs carry frozen membership in their native serialized component predicate, not a reload-time tag. */
public final class FamilyCosts {
 public static final String MARKER="human_journey:payment_v1";
 public static ItemCost cost(List<String> members,int count){
  if(members.isEmpty()||members.size()>256)throw new IllegalArgumentException("invalid payment members");for(String id:members){var key=ResourceLocation.parse(id);if(!BuiltInRegistries.ITEM.containsKey(key)||count>BuiltInRegistries.ITEM.getValue(key).getDefaultInstance().getMaxStackSize())throw new IllegalArgumentException("payment exceeds actual member stack: "+id);}
  var data=new CompoundTag();var list=new ListTag();for(String id:members)list.add(StringTag.valueOf(id));data.put(MARKER,list);
  return new ItemCost(BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(members.getFirst())).builtInRegistryHolder(),count,DataComponentExactPredicate.expect(DataComponents.CUSTOM_DATA,CustomData.of(data)));
 }
 public static boolean marked(ItemStack display){var data=display.get(DataComponents.CUSTOM_DATA);return data!=null&&data.copyTag().contains(MARKER);}
 public static boolean accepts(ItemCost cost,ItemStack offered){if(offered.isEmpty()||offered.getMaxStackSize()<cost.count())return false;var data=cost.itemStack().get(DataComponents.CUSTOM_DATA).copyTag();var members=data.getListOrEmpty(MARKER);if(members.isEmpty()||members.size()>256)return false;String id=BuiltInRegistries.ITEM.getKey(offered.getItem()).toString();for(var member:members)if(member.asString().orElse("").equals(id))return true;return false;}
 public static ItemCost preserve(ItemStack display){return marked(display)?new ItemCost(display.getItem().builtInRegistryHolder(),display.getCount(),DataComponentExactPredicate.expect(DataComponents.CUSTOM_DATA,display.get(DataComponents.CUSTOM_DATA))):new ItemCost(display.getItem(),Math.max(1,display.getCount()));}
 private FamilyCosts(){}
}
