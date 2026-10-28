package org.humanjourney.core.water;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.*;
import org.humanjourney.core.water.WaterPolicy.Quality;

public final class WaterContainers {
    private static final String[] TIERS={"leather","copper","iron","gold","diamond","netherite"};
    public record Kind(Quality quality,String tier) { public boolean canteen(){return tier!=null;} }
    public static Kind kind(ItemStack stack){
        if(stack.isEmpty()) return null;
        var id=BuiltInRegistries.ITEM.getKey(stack.getItem());
        if(stack.is(Items.POTION)){
            var potion=stack.get(DataComponents.POTION_CONTENTS);
            return potion!=null && potion.is(Potions.WATER) && !potion.hasEffects()?new Kind(Quality.NORMAL,null):null;
        }
        if(!id.getNamespace().equals("toughasnails")) return null;
        if(id.getPath().equals("dirty_water_bottle")) return new Kind(Quality.DIRTY,null);
        if(id.getPath().equals("purified_water_bottle")) return new Kind(Quality.PURIFIED,null);
        for(String tier:TIERS){
            if(id.getPath().equals(tier+"_dirty_water_canteen")) return new Kind(Quality.DIRTY,tier);
            if(id.getPath().equals(tier+"_water_canteen")) return new Kind(Quality.NORMAL,tier);
            if(id.getPath().equals(tier+"_purified_water_canteen")) return new Kind(Quality.PURIFIED,tier);
        }
        return null;
    }
    /** Includes non-water potions so legacy broad furnace ingredients cannot destroy them. */
    public static boolean heatGuard(ItemStack stack){return kind(stack)!=null || stack.is(Items.POTION);}
    public static ItemStack preserve(ItemStack original,Item replacement){return original.transmuteCopy(replacement,1);}
    public static ItemStack convert(ItemStack input,Quality target){
        Kind kind=kind(input);
        if(kind==null || (kind.canteen() && (input.getCount()!=1 || !input.isDamageableItem() || input.getDamageValue()>=input.getMaxDamage()))) return ItemStack.EMPTY;
        Item item;
        if(!kind.canteen() && target==Quality.NORMAL) item=Items.POTION;
        else{
            String path=kind.canteen()?kind.tier()+switch(target){case DIRTY->"_dirty_water_canteen";case NORMAL->"_water_canteen";case PURIFIED->"_purified_water_canteen";}:target==Quality.DIRTY?"dirty_water_bottle":"purified_water_bottle";
            var id=ResourceLocation.fromNamespaceAndPath("toughasnails",path);
            if(!BuiltInRegistries.ITEM.containsKey(id)) return ItemStack.EMPTY;
            item=BuiltInRegistries.ITEM.getValue(id);
        }
        ItemStack result=preserve(input,item);
        if(!kind.canteen() && target==Quality.NORMAL) result.set(DataComponents.POTION_CONTENTS,new PotionContents(Potions.WATER));
        if(kind.canteen() && (result.getDamageValue()!=input.getDamageValue() || result.getMaxDamage()!=input.getMaxDamage())) return ItemStack.EMPTY;
        return result;
    }
    public static ItemStack boiled(ItemStack input){var k=kind(input);return k!=null && WaterPolicy.canBoil(k.quality())?convert(input,Quality.PURIFIED):ItemStack.EMPTY;}
    public static String describe(ItemStack stack){var k=kind(stack);if(k==null)return "not a supported filled water container";return k.quality()+" "+(k.canteen()?k.tier()+" canteen | remaining="+(stack.getMaxDamage()-stack.getDamageValue())+"/"+stack.getMaxDamage():"bottle");}
    private WaterContainers(){}
}
