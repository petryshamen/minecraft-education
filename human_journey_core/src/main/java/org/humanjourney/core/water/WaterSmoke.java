package org.humanjourney.core.water;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.*;
import net.minecraft.data.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.*;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import java.util.concurrent.CompletableFuture;

/** Opt-in datagen integration checks. Never called in ordinary gameplay and never starts a world. */
public final class WaterSmoke {
    public static void register(BusGroup group){
        GatherDataEvent.getBus(group).addListener(event->event.getGenerator().addProvider(true,new DataProvider(){
            public CompletableFuture<?> run(CachedOutput output){return event.getLookupProvider().thenAccept(WaterSmoke::verify);}
            public String getName(){return "Human Journey water state preservation smoke";}
        }));
    }
    private static void check(boolean condition,String message){if(!condition)throw new IllegalStateException("Water smoke: "+message);}
    private static ItemStack item(String id){var key=ResourceLocation.parse(id);check(BuiltInRegistries.ITEM.containsKey(key),"missing "+id);return new ItemStack(BuiltInRegistries.ITEM.getValue(key));}
    private static void verify(HolderLookup.Provider lookup){
        // Datagen lookups use unowned bootstrap holders. Use a real frozen registry for persistence fixtures,
        // as a loaded world does, instead of trying to serialize those unowned data-generation references.
        var fixtureEnchantments=new MappedRegistry<Enchantment>(Registries.ENCHANTMENT,Lifecycle.stable());
        fixtureEnchantments.register(Enchantments.UNBREAKING,lookup.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING).value(),RegistrationInfo.BUILT_IN);
        fixtureEnchantments.freeze();
        lookup=HolderLookup.Provider.create(java.util.stream.Stream.concat(lookup.listRegistries().filter(registry->!registry.key().equals(Registries.ENCHANTMENT)),java.util.stream.Stream.of(fixtureEnchantments)));
        var ops=RegistryOps.create(NbtOps.INSTANCE,lookup); // World saves use NBT; JSON narrows custom numeric NBT tag types.
        var enchant=lookup.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING);
        String[] tiers={"leather","copper","iron","gold","diamond","netherite"};int[] capacities={5,6,7,10,15,25};int checked=0;
        for(int t=0;t<tiers.length;t++)for(int used:new int[]{0,capacities[t]/2,capacities[t]-1})for(boolean dirty:new boolean[]{true,false}){
            var input=item("toughasnails:"+tiers[t]+(dirty?"_dirty_water_canteen":"_water_canteen"));
            check(input.getMaxDamage()==capacities[t],"tier capacity");input.setDamageValue(used);input.enchant(enchant,2);
            input.set(DataComponents.CUSTOM_NAME,Component.literal("Student water "+tiers[t]));
            CompoundTag data=new CompoundTag();data.putString("owner","student");data.putInt("remaining_marker",capacities[t]-used);input.set(DataComponents.CUSTOM_DATA,CustomData.of(data));
            var target=dirty?WaterPolicy.Quality.NORMAL:WaterPolicy.Quality.PURIFIED;
            var result=WaterContainers.convert(input,target);
            check(!result.isEmpty()&&result.getCount()==1,"one input, one output");
            check(result.getComponentsPatch().equals(input.getComponentsPatch()),"all component overrides preserved");
            check(result.getDamageValue()==used&&result.getMaxDamage()==capacities[t],"remaining drinks/material preserved");
            var json=ItemStack.CODEC.encodeStart(ops,result).getOrThrow();var restored=ItemStack.CODEC.parse(ops,json).getOrThrow();
            check(ItemStack.isSameItemSameComponents(result,restored),"actual TAN stack registry/codec round trip: "+result.getComponentsPatch()+" vs "+restored.getComponentsPatch());
            if(dirty)check(WaterContainers.boiled(input).isEmpty(),"dirty cannot boil");
            else{
                var slots=NonNullList.withSize(3,ItemStack.EMPTY);slots.set(0,input.copy());
                check(WaterTreatment.burn(slots,64),"dynamic furnace conversion");
                check(slots.get(0).isEmpty()&&ItemStack.isSameItemSameComponents(slots.get(2),result),"furnace preserves exact output state");
                slots.set(0,input.copy());check(!WaterTreatment.canOutput(slots.get(0),new ItemStack(Items.STONE),64),"blocked output cannot consume input");
                var fire=new CampfireBlockEntity(BlockPos.ZERO,Blocks.CAMPFIRE.defaultBlockState());var access=(CampfireAccess)fire;
                access.hjItems().set(0,input.copy());access.hjProgress()[0]=123;access.hjTimes()[0]=500;
                var saved=fire.saveWithFullMetadata(lookup);
                var loaded=(CampfireAccess)BlockEntity.loadStatic(BlockPos.ZERO,Blocks.CAMPFIRE.defaultBlockState(),saved,lookup);
                check(loaded!=null&&ItemStack.isSameItemSameComponents(loaded.hjItems().get(0),input)&&loaded.hjProgress()[0]==123&&loaded.hjTimes()[0]==500,"campfire save/load preserves water and progress");
            }
            checked++;
        }
        check(WaterContainers.kind(PotionContents.createItemStack(Items.POTION,Potions.HEALING))==null,"non-water potion is not normal water");
        var bottle=WaterContainers.convert(item("toughasnails:dirty_water_bottle"),WaterPolicy.Quality.NORMAL);
        check(WaterContainers.kind(bottle).quality()==WaterPolicy.Quality.NORMAL,"dirty bottle -> normal water potion");
        check(WaterContainers.kind(WaterContainers.boiled(bottle)).quality()==WaterPolicy.Quality.PURIFIED,"normal bottle -> purified");
        LogUtils.getLogger().info("[HJ Core] WATER_STATE_SMOKE_OK: {} actual TAN canteen conversions + codecs; furnace outputs; campfire persistence; bottle chain",checked);
    }
    private WaterSmoke(){}
}
