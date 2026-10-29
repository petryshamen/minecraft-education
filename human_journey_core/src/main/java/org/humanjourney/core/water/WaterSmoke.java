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
        // Datagen does not run a world's tag reload. Bind only this isolated process's real sand fixture,
        // so TAN's unmodified filter-duration lookup can enumerate its required vanilla tag.
        BuiltInRegistries.ITEM.freeze(); // Forge leaves datagen registries unfrozen; PendingTags requires a frozen owner.
        BuiltInRegistries.ITEM.prepareTagReload(new net.minecraft.tags.TagLoader.LoadResult<Item>(Registries.ITEM,
            java.util.Map.of(net.minecraft.tags.ItemTags.SAND,java.util.List.of(Items.SAND.builtInRegistryHolder(),Items.RED_SAND.builtInRegistryHolder())))).apply();
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
        int saltCases=0;
        for(int t=0;t<tiers.length;t++){
            var empty=item("toughasnails:empty_"+tiers[t]+"_canteen");
            empty.enchant(enchant,2);empty.set(DataComponents.CUSTOM_NAME,Component.literal("Ocean expedition"));
            CompoundTag owner=new CompoundTag();owner.putString("owner","student");empty.set(DataComponents.CUSTOM_DATA,CustomData.of(owner));
            var filled=WaterContainers.fill(empty,WaterPolicy.Quality.SALT);
            check(WaterContainers.kind(filled).quality()==WaterPolicy.Quality.SALT,"salt canteen fill");
            check(filled.getMaxDamage()==capacities[t]&&filled.getDamageValue()==0,"salt fill correct tier/full");
            filled.setDamageValue(capacities[t]-1);
            var saved=ItemStack.CODEC.encodeStart(ops,filled).getOrThrow();var moved=ItemStack.CODEC.parse(ops,saved).getOrThrow();
            WaterBalance.accept(WaterPolicy.parse(com.google.gson.JsonParser.parseString("{\"classification\":{\"salt_biome_tags\":[]}}")),"freshwater destination policy");
            check(WaterContainers.kind(moved).quality()==WaterPolicy.Quality.SALT,"salt persists regardless of current biome/tag policy");
            check(moved.getDamageValue()==capacities[t]-1&&moved.getEnchantments().equals(empty.getEnchantments())&&moved.get(DataComponents.CUSTOM_NAME).equals(empty.get(DataComponents.CUSTOM_NAME)),"salt preserves fill/name/enchantments");
            check(moved.get(DataComponents.CUSTOM_DATA).copyTag().getString("owner").orElse("").equals("student"),"salt preserves unrelated data");
            check(WaterContainers.boiled(moved).isEmpty()&&WaterContainers.convert(moved,WaterPolicy.Quality.NORMAL).isEmpty()&&WaterPurifier.purifiedFill(moved).isEmpty(),"salt cannot boil/filter/purify");
            var saltSlots=NonNullList.withSize(3,ItemStack.EMPTY);saltSlots.set(0,moved.copy());
            check(!WaterTreatment.burn(saltSlots,64)&&ItemStack.isSameItemSameComponents(saltSlots.get(0),moved)&&saltSlots.get(2).isEmpty(),"furnace cannot consume or purify salt");
            var emptied=WaterContainers.emptied(moved);check(!SaltWater.marked(emptied)&&WaterContainers.empty(emptied),"salt last drink leaves correct empty container");
            check(emptied.get(DataComponents.CUSTOM_NAME).equals(empty.get(DataComponents.CUSTOM_NAME))&&emptied.getEnchantments().equals(empty.getEnchantments()),"empty keeps identity");
            var pure=WaterPurifier.purifiedFill(emptied);check(WaterContainers.kind(pure).quality()==WaterPolicy.Quality.PURIFIED&&WaterContainers.kind(pure).tier().equals(tiers[t])&&pure.getDamageValue()==0,"purifier empty canteen -> correct tier full purified");
            var machine=new toughasnails.block.entity.WaterPurifierBlockEntity(BlockPos.ZERO,toughasnails.api.block.TANBlocks.WATER_PURIFIER.defaultBlockState());
            var machineAccess=(PurifierAccess)machine;machine.setItem(1,new ItemStack(Items.CHARCOAL));machine.setItem(0,empty.copy());
            check(!WaterPurifier.process(machine,false)&&machine.getItem(1).getCount()==1&&machineAccess.hjRemaining()==0,"invalid intake spends no filter/input");
            check(WaterPurifier.process(machine,true)&&machine.getItem(0).isEmpty()&&machineAccess.hjRemaining()==1500,"instant canteen fill spends TAN charcoal units once");
            check(ItemStack.isSameItemSameComponents(machine.getItem(2),pure),"GUI transaction preserves all components");
            var persisted=machine.saveWithFullMetadata(lookup);var restored=(toughasnails.block.entity.WaterPurifierBlockEntity)BlockEntity.loadStatic(BlockPos.ZERO,machine.getBlockState(),persisted,lookup);
            check(restored!=null&&((PurifierAccess)restored).hjRemaining()==1500&&ItemStack.isSameItemSameComponents(restored.getItem(2),pure),"purifier capacity/output persistence");
            restored.setItem(0,new ItemStack(Items.GLASS_BOTTLE));
            check(!WaterPurifier.process(restored,true)&&((PurifierAccess)restored).hjRemaining()==1500,"incompatible output does not spend filter");
            restored.setItem(2,ItemStack.EMPTY);check(WaterPurifier.process(restored,true)&&((PurifierAccess)restored).hjRemaining()==1400,"remaining stored capacity charged exactly once");
            check(WaterContainers.kind(restored.getItem(2)).quality()==WaterPolicy.Quality.PURIFIED,"empty glass bottle -> purified bottle");
            restored.setItem(2,ItemStack.EMPTY);restored.setItem(0,moved.copy());check(!WaterPurifier.process(restored,true)&&((PurifierAccess)restored).hjRemaining()==1400,"salt GUI bypass refused");
            saltCases++;
        }
        var saltBottle=WaterContainers.fill(new ItemStack(Items.GLASS_BOTTLE),WaterPolicy.Quality.SALT);
        var saltBottleSaved=ItemStack.CODEC.encodeStart(ops,saltBottle).getOrThrow();
        check(WaterContainers.kind(ItemStack.CODEC.parse(ops,saltBottleSaved).getOrThrow()).quality()==WaterPolicy.Quality.SALT,"salt bottle codec persistence");
        check(WaterContainers.boiled(saltBottle).isEmpty()&&WaterContainers.convert(saltBottle,WaterPolicy.Quality.NORMAL).isEmpty(),"salt bottle treatment refused");
        var thirst=new toughasnails.thirst.ThirstData();thirst.setThirst(10);thirst.setHydration(2);thirst.setExhaustion(0);
        WaterDrinking.applySalt(thirst,WaterExtensions.defaults().salt());check(thirst.getThirst()==10&&thirst.getHydration()==2&&thirst.getExhaustion()==12,"salt zero restoration and harmful exhaustion");
        WaterDrinking.applySalt(thirst,new WaterExtensions.Salt(-2,-1,20,false,0,0));check(thirst.getThirst()==8&&thirst.getHydration()==1&&thirst.getExhaustion()==32,"custom salt balance applied");
        try{
            check(toughasnails.item.DrinkItem.class.getMethod("onUseTick",net.minecraft.world.level.Level.class,net.minecraft.world.entity.LivingEntity.class,ItemStack.class,int.class).getDeclaringClass()==toughasnails.item.DrinkItem.class,"TAN bottle sound override applied");
        }catch(ReflectiveOperationException e){throw new IllegalStateException(e);}
        for(String drink:new String[]{"dirty_water_bottle","purified_water_bottle","apple_juice","cactus_juice","chorus_fruit_juice","glow_berry_juice","melon_juice","pumpkin_juice","sweet_berry_juice"})check(!item("toughasnails:"+drink).has(DataComponents.CONSUMABLE),"TAN bottle lacks native Consumable sounds");
        WaterBalance.reset();
        LogUtils.getLogger().info("[HJ Core] SALT_PURIFIER_SMOKE_OK: {} tiers, salt canteens/bottle codecs, purifier transactions/capacity/codecs, salt drinking, bottle sound hook; no world started",saltCases);
        LogUtils.getLogger().info("[HJ Core] WATER_STATE_SMOKE_OK: {} actual TAN canteen conversions + codecs; furnace outputs; campfire persistence; bottle chain",checked);
    }
    private WaterSmoke(){}
}
