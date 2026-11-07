package org.humanjourney.core.npc.economy;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.*;
import net.minecraft.world.item.ItemStack;
public final class EconomyItems {
 public static ItemStack stack(String id,int count,JsonObject components,HolderLookup.Provider registries){
  var key=ResourceLocation.parse(id);if(!BuiltInRegistries.ITEM.containsKey(key)||id.equals("minecraft:air"))throw new IllegalArgumentException("unknown item: "+id);
  var json=new JsonObject();json.addProperty("id",id);json.addProperty("count",count);if(!components.isEmpty())json.add("components",components.deepCopy());
  var result=ItemStack.CODEC.parse(RegistryOps.create(JsonOps.INSTANCE,registries),json).getOrThrow();if(result.isEmpty()||count>result.getMaxStackSize())throw new IllegalArgumentException("invalid output count/components: "+id);return result;
 }
 private EconomyItems(){}
}
