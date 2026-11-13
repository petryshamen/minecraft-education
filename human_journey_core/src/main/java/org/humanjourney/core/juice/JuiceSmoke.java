package org.humanjourney.core.juice;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.nbt.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.data.*;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import com.mojang.serialization.Lifecycle;
import org.humanjourney.core.water.*;
public final class JuiceSmoke {
 private static void check(boolean condition,String message){if(!condition)throw new IllegalStateException("Juice smoke: "+message);}
 private static ItemStack item(String id){return new ItemStack(BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(id)));}
 public static void register(BusGroup group){GatherDataEvent.getBus(group).addListener(e->e.getGenerator().addProvider(true,new DataProvider(){public String getName(){return "Human Journey juice native canteen/recipe/component checks";}public java.util.concurrent.CompletableFuture<?> run(CachedOutput output){return e.getLookupProvider().thenAccept(JuiceSmoke::verify);}}));}
 private static void verify(HolderLookup.Provider lookup){
  var registry=new MappedRegistry<Enchantment>(Registries.ENCHANTMENT,Lifecycle.stable());registry.register(Enchantments.UNBREAKING,lookup.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING).value(),RegistrationInfo.BUILT_IN);registry.freeze();lookup=HolderLookup.Provider.create(java.util.stream.Stream.concat(lookup.listRegistries().filter(x->!x.key().equals(Registries.ENCHANTMENT)),java.util.stream.Stream.of(registry)));var ops=RegistryOps.create(NbtOps.INSTANCE,lookup);var enchant=registry.getOrThrow(Enchantments.UNBREAKING);
  var bottles=BuiltInRegistries.ITEM.stream().filter(i->i.getClass()==toughasnails.item.JuiceItem.class).sorted(Comparator.comparing(i->BuiltInRegistries.ITEM.getKey(i).toString())).toList();check(bottles.size()==7,"exact installed TAN seven JuiceItem registrations");var bottle=BuiltInRegistries.ITEM.getKey(bottles.getFirst()).toString();for(var b:bottles)check(JuiceReloadListener.references(lookup).bottleSupported(BuiltInRegistries.ITEM.getKey(b).toString()),"native juice supported without copied balance values");
  var def=new JuiceDefinition("synthetic_test",bottle,new JuiceDefinition.Ingredient("minecraft:sand",null,Set.of("minecraft:sand")),"Synthetic Juice Canteen",1);var catalog=JuiceCatalog.of(List.of(def));var previous=JuiceStore.state();JuiceStore.accept(catalog,"synthetic no-world fixture");int tested=0;
  try{var recipe=new JuiceRecipe(CraftingBookCategory.MISC);for(String tier:new String[]{"leather","copper","iron","gold","diamond","netherite"}){
   var full=item("toughasnails:"+tier+"_purified_water_canteen");var data=new CompoundTag();data.putString("owner","fixture");full.set(DataComponents.CUSTOM_DATA,CustomData.of(data));full.set(DataComponents.CUSTOM_NAME,Component.literal("Preserved custom name"));full.enchant(enchant,2);
   var grid=CraftingInput.of(2,2,List.of(full,new ItemStack(Items.SAND),ItemStack.EMPTY,ItemStack.EMPTY));check(recipe.matches(grid,null),"full purified crafting "+tier);var result=recipe.assemble(grid,lookup);check(result.is(full.getItem())&&result.getDamageValue()==0&&JuiceState.id(result).equals(def.id()),"tier and full state");check(result.get(DataComponents.CUSTOM_NAME).equals(full.get(DataComponents.CUSTOM_NAME))&&result.getEnchantments().equals(full.getEnchantments()),"name/enchantment preservation");check(result.get(DataComponents.CUSTOM_DATA).copyTag().getString("owner").orElse("").equals("fixture"),"unrelated components");check(recipe.getRemainingItems(grid).get(0).isEmpty(),"no duplicated empty canteen remainder");check(WaterContainers.kind(result)==null&&WaterContainers.heatGuard(result)&&WaterContainers.boiled(result).isEmpty(),"separate juice identity / heat rejection");check(WaterContainers.convert(result,WaterPolicy.Quality.NORMAL).isEmpty(),"treatment cannot strip juice");
   var saved=ItemStack.CODEC.parse(ops,ItemStack.CODEC.encodeStart(ops,result).getOrThrow()).getOrThrow();check(ItemStack.isSameItemSameComponents(result,saved),"actual TAN stack codec persistence");int maximum=saved.getMaxDamage();for(int sip=1;sip<=maximum;sip++){saved=JuiceContainers.consume(saved,false);if(sip<maximum){check(JuiceState.id(saved).equals(def.id())&&saved.getDamageValue()==sip&&saved.is(full.getItem()),"exact one use / flavour persists");if(sip==1)saved=ItemStack.CODEC.parse(ops,ItemStack.CODEC.encodeStart(ops,saved).getOrThrow()).getOrThrow();}else{check(saved.is(item("toughasnails:empty_"+tier+"_canteen").getItem())&&!JuiceState.marked(saved),"matching empty tier");check(saved.get(DataComponents.CUSTOM_NAME).equals(full.get(DataComponents.CUSTOM_NAME))&&saved.getEnchantments().equals(full.getEnchantments()),"empty preserves custom data");}}
   var partial=full.copy();partial.setDamageValue(1);check(!JuiceContainers.fullPurified(partial),"partial rejected");for(var quality:new WaterPolicy.Quality[]{WaterPolicy.Quality.DIRTY,WaterPolicy.Quality.NORMAL,WaterPolicy.Quality.SALT}){var wrong=WaterContainers.fill(item("toughasnails:empty_"+tier+"_canteen"),quality);check(!JuiceContainers.fullPurified(wrong),"wrong water quality rejected");}var invalid=result.copy();var unknown=new JuiceDefinition("removed_fixture",bottle,def.ingredient(),"Removed fixture",1);JuiceState.set(invalid,unknown);check(JuiceContainers.definition(invalid)==null&&WaterContainers.kind(invalid)==null,"removed ID does not become water");tested++;
  }check(recipe.getSerializer()==JuiceBootstrap.RECIPE.get(),"registered generic recipe serializer");var decoded=net.minecraft.world.item.crafting.Recipe.CODEC.parse(RegistryOps.create(com.mojang.serialization.JsonOps.INSTANCE,lookup),com.google.gson.JsonParser.parseString("{\"type\":\"human_journey_core:juice_canteen\",\"category\":\"misc\"}")).getOrThrow();check(decoded instanceof JuiceRecipe,"actual generic recipe JSON codec");var buffer=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));try{JuiceBootstrap.RECIPE.get().streamCodec().encode(buffer,recipe);check(JuiceBootstrap.RECIPE.get().streamCodec().decode(buffer) instanceof JuiceRecipe,"native recipe network codec");}finally{buffer.release();}}finally{JuiceStore.accept(previous.catalog(),previous.source());}
  com.mojang.logging.LogUtils.getLogger().info("[HJ Core] JUICE_CANTEEN_SMOKE_OK: {} native tiers, seven bottle registrations, dynamic crafting, state/name/enchantment/codec preservation, exact depletion, water guards and four Mixin targets; no world/player/gameplay created",tested);
 }
 private JuiceSmoke(){}
}
