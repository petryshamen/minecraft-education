package org.humanjourney.core.npc.economy;
import org.humanjourney.core.npc.context.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.trading.*;
import java.util.*;
public record EconomySpawn(MerchantOffers offers,EconomySettings settings,NpcContext context,GeoProfile profile,EconomyGenerator.Plan plan){
 public static EconomySpawn prepare(NpcContextStore.Data data,String role,NpcContext context,HolderLookup.Provider registries){
  return prepare(data,role,context,registries,java.util.concurrent.ThreadLocalRandom.current());
 }
 public static EconomySpawn prepare(NpcContextStore.Data data,String role,NpcContext context,HolderLookup.Provider registries,java.util.random.RandomGenerator random){
  var settings=data.archetypes().get(role).economy(context);var profile=data.geography().profile(context.macroRegion(),context.localArea());var catalog=data.economy();var plan=EconomyGenerator.generate(catalog,role,context,profile,settings,(id,components)->EconomyItems.stack(id,1,components,registries).getMaxStackSize(),random);return new EconomySpawn(nativeOffers(catalog,plan,registries),settings,context,profile,plan);
 }
 public static MerchantOffers nativeOffers(EconomyCatalog catalog,EconomyGenerator.Plan plan,HolderLookup.Provider registries){var result=new MerchantOffers();for(var entry:plan.offers()){
  var solved=entry.barter();var a=catalog.commodities().get(solved.first().good().id());var first=FamilyCosts.cost(catalog.members(a),solved.first().count());Optional<ItemCost> second=Optional.empty();if(solved.second()!=null){var b=catalog.commodities().get(solved.second().good().id());second=Optional.of(FamilyCosts.cost(catalog.members(b),solved.second().count()));}
  var output=EconomyItems.stack(entry.concreteOutput(),solved.outputCount(),entry.output().components(),registries);var offer=new MerchantOffer(first,second,output,0,entry.output().maxUses(),10,0F);if(!offer.shouldRewardExp())throw new IllegalStateException("native trade XP must be enabled");result.add(offer);
 }return result;}
}
