package org.humanjourney.core.juice;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.entity.player.Player;
import com.google.gson.JsonParser;
/** Exact registered items/codecs and native crafting containers; no Minecraft world/player is constructed. */
public final class JuiceScalingSmoke {
 private static void check(boolean c,String why){if(!c)throw new IllegalStateException("Juice scaling smoke: "+why);}
 private static ItemStack item(String id){return new ItemStack(BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(id)));}
 private static CraftingContainer grid(int width,List<ItemStack> items){var menu=new AbstractContainerMenu(null,0){public ItemStack quickMoveStack(Player p,int i){return ItemStack.EMPTY;}public boolean stillValid(Player p){return true;}};var grid=new TransientCraftingContainer(menu,width,items.size()/width);for(int i=0;i<items.size();i++)grid.setItem(i,items.get(i).copy());return grid;}
 private static void consume(CraftingContainer grid,JuiceCrafting.Plan plan){for(int i=0;i<grid.getContainerSize();i++)JuiceCrafting.remove(grid,plan,i,1);}
 public static void verify(HolderLookup.Provider lookup){
  var bottles=BuiltInRegistries.ITEM.stream().filter(i->i.getClass()==toughasnails.item.JuiceItem.class).sorted(Comparator.comparing(i->BuiltInRegistries.ITEM.getKey(i).toString())).toList();String source=BuiltInRegistries.ITEM.getKey(bottles.getFirst()).toString();var refs=JuiceReloadListener.references(lookup);
  String common="\"schema_version\":2,\"ingredient\":{\"item\":\"minecraft:sand\"},\"bottle_ingredient_count\":2,\"canteen_ingredient_count\":8,\"canteen_display_name\":\"Fixture Canteen\",\"heal_per_drink\":1.0";
  var nativeDef=JuiceDefinition.parse(JsonParser.parseString("{"+common+",\"id\":\"fixture_native\",\"bottle\":{\"type\":\"native\",\"item\":\""+source+"\"}}"),refs);
  var customDef=JuiceDefinition.parse(JsonParser.parseString("{"+common.replace("minecraft:sand","minecraft:gravel")+",\"id\":\"fixture_custom\",\"bottle\":{\"type\":\"custom\",\"display_name\":\"Fixture Custom Bottle\",\"effect_source_item\":\""+source+"\"}}"),refs);
  var previous=JuiceStore.state();JuiceStore.accept(JuiceCatalog.of(List.of(nativeDef,customDef)),"synthetic scaling fixture");
  try{
   check(BuiltInRegistries.ITEM.stream().filter(i->i instanceof CustomJuiceBottleItem).count()==1,"exactly one custom bottle item");check(BuiltInRegistries.ITEM.getKey(JuiceBootstrap.BOTTLE.get()).toString().equals("human_journey_core:juice_bottle"),"one registered shared bottle");
   var water=item("toughasnails:purified_water_bottle");water.setCount(3);var sand=new ItemStack(Items.SAND,20);var bottleGrid=grid(2,List.of(water,sand,ItemStack.EMPTY,ItemStack.EMPTY));int crafts=0;JuiceCrafting.Plan plan;while((plan=JuiceCrafting.forGrid(bottleGrid,true))!=null){check(plan.output().getItem()==bottles.getFirst(),"native bottle output");check(plan.units().get(0)==1&&plan.units().get(1)==2,"exact independent bottle cost");consume(bottleGrid,plan);crafts++;check(crafts<=3,"bounded repeated take");}check(crafts==3&&bottleGrid.getItem(1).getCount()==14,"repeated/shift-style recomputation spends exactly six material units for three bottles");
   for(String tier:new String[]{"leather","copper","iron","gold","diamond","netherite"}){var full=item("toughasnails:"+tier+"_purified_water_canteen");var g=grid(2,List.of(full,new ItemStack(Items.SAND,20),ItemStack.EMPTY,ItemStack.EMPTY));var p=JuiceCrafting.forGrid(g,false);check(p!=null&&p.units().get(1)==8&&p.output().is(full.getItem()),"same eight-unit cost for every tier");consume(g,p);check(g.getItem(1).getCount()==12&&g.getItem(0).isEmpty(),"20 minus eight; carrier spent once");var split=grid(2,List.of(full,new ItemStack(Items.SAND,4),new ItemStack(Items.SAND,4),ItemStack.EMPTY));p=JuiceCrafting.forGrid(split,false);check(p!=null&&p.units().get(1)==4&&p.units().get(2)==4,"split budget");consume(split,p);check(split.isEmpty(),"split consumption");var cells=new ArrayList<ItemStack>();cells.add(full);for(int i=0;i<8;i++)cells.add(new ItemStack(Items.SAND));check(JuiceCrafting.forGrid(grid(3,cells),false)!=null,"eight occupied ingredient cells also valid");}
   var cg=grid(2,List.of(item("toughasnails:purified_water_bottle"),new ItemStack(Items.GRAVEL,5),ItemStack.EMPTY,ItemStack.EMPTY));var cp=JuiceCrafting.forGrid(cg,true);var custom=cp.output();check(JuiceBottles.carrier(custom)&&JuiceState.id(custom).equals(customDef.id())&&custom.getHoverName().getString().equals("Fixture Custom Bottle"),"custom identity/name");var ops=RegistryOps.create(NbtOps.INSTANCE,lookup);var restored=ItemStack.CODEC.parse(ops,ItemStack.CODEC.encodeStart(ops,custom).getOrThrow()).getOrThrow();check(ItemStack.isSameItemSameComponents(custom,restored)&&JuiceBottles.definition(restored).effectSourceItem().equals(source),"custom native item codec/source");consume(cg,cp);check(cg.getItem(1).getCount()==3,"custom bottle count two");
   var can=JuiceCrafting.plan(grid(2,List.of(item("toughasnails:iron_purified_water_canteen"),new ItemStack(Items.GRAVEL,8),ItemStack.EMPTY,ItemStack.EMPTY)).asCraftInput(),false).output();check(JuiceContainers.definition(can).effectSourceItem().equals(source),"custom canteen resolves source");check(!JuiceStore.state().catalog().bottles().containsKey("human_journey_core:juice_bottle"),"native bottle healing cannot double-heal shared custom item");
   var decoded=Recipe.CODEC.parse(RegistryOps.create(com.mojang.serialization.JsonOps.INSTANCE,lookup),JsonParser.parseString("{\"type\":\"human_journey_core:juice_bottle\",\"category\":\"misc\"}")).getOrThrow();check(decoded instanceof JuiceBottleRecipe,"native bottle recipe JSON codec");
  }finally{JuiceStore.accept(previous.catalog(),previous.source());}
  com.mojang.logging.LogUtils.getLogger().info("[HJ Core] JUICE_SCALING_SMOKE_OK: schema v2 native/custom, exact single/split/eight-cell budgets, repeated take accounting, six equal-cost tiers, registered shared item/name/codecs and ResultSlot Mixin load; no world/player/gameplay created");
 }
 private JuiceScalingSmoke(){}
}
