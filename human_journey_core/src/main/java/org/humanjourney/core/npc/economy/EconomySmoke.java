package org.humanjourney.core.npc.economy;
import java.util.*;
import java.nio.file.*;
import java.util.concurrent.CompletableFuture;
import java.lang.reflect.*;
import com.mojang.serialization.*;
import net.minecraft.data.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.*;
import net.minecraft.nbt.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.trading.*;
import net.minecraft.world.level.storage.*;
import net.minecraft.util.ProblemReporter;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import de.markusbordihn.easynpc.entity.easynpc.data.TradingDataCapable;
import de.markusbordihn.easynpc.data.trading.TradingDataSet;
import de.markusbordihn.easynpc.data.trading.TradingType;
import de.markusbordihn.easynpc.data.synched.SynchedDataIndex;
import org.humanjourney.core.npc.context.*;
/** Native data-only round trip through Easy NPC save/load hooks. Never creates an entity or world. */
public final class EconomySmoke {
 static void check(boolean yes,String message){if(!yes)throw new IllegalStateException(message);}
 static List<NpcContextStore.Source> files(Path root,String prefix)throws Exception{List<NpcContextStore.Source> result=new ArrayList<>();try(var files=Files.list(root.resolve(prefix))){for(var p:files.sorted().toList())result.add(new NpcContextStore.Source("human_journey:"+p.getFileName().toString().replace(".json",""),"synthetic test",Files.readString(p)));}return result;}
 public static void register(BusGroup group){GatherDataEvent.getBus(group).addListener(event->event.getGenerator().addProvider(true,new DataProvider(){
  public String getName(){return "Human Journey synthetic economy native data checks";}
  public CompletableFuture<?> run(CachedOutput output){return event.getLookupProvider().thenAccept(lookup->{try{
   Class.forName("net.minecraft.world.item.trading.ItemCost");Class.forName("de.markusbordihn.easynpc.entity.easynpc.data.TradingDataCapable");
   Path root=Path.of("../../example-economy-datapack/data/human_journey");var data=NpcContextStore.prepare(new NpcContextStore.Source("human_journey:core/npc_context","synthetic",Files.readString(root.resolve("core/npc_context.json"))),files(root,"npc_geography"),files(root,"npc_archetype"),NpcContextReloadListener::itemLimit,files(root,"npc_commodity").stream().map(s->new EconomyCatalog.Source(s.id(),s.json())).toList(),files(root,"npc_family").stream().map(s->new EconomyCatalog.Source(s.id(),s.json())).toList(),null,(id,components)->EconomyItems.stack(id,1,components,lookup).getMaxStackSize());
   var context=new NpcContext("era_test","none","test_macro","test_local","none");var spawn=EconomySpawn.prepare(data,"human_journey:test_trader",context,lookup);check(spawn.plan().matchingSellers()==1&&spawn.offers().size()==1,"seller generation count");var offer=spawn.offers().getFirst();check(offer.getXp()==10&&offer.shouldRewardExp()&&offer.getMaxUses()==7&&offer.getUses()==0,"native XP/stock");check(offer.getItemCostA().count()==5&&offer.getResult().getCount()==2,"2 vs 5 exact value solve");var family=data.economy().families().get("human_journey:test_log");
   for(String member:family.members()){var stack=new ItemStack(BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(member)),offer.getItemCostA().count());check(offer.satisfiedBy(stack,ItemStack.EMPTY),"native family matching: "+member);}check(!offer.satisfiedBy(offer.getResult(),ItemStack.EMPTY),"invalid family member accepted");
   var state=new EnumMap<SynchedDataIndex,Object>(SynchedDataIndex.class);state.put(SynchedDataIndex.TRADING_MERCHANT_OFFERS,spawn.offers());var trading=new TradingDataSet();trading.setType(TradingType.ADVANCED);state.put(SynchedDataIndex.TRADING_DATA_SET,trading);MerchantOffers[] runtime={null};
   var npc=(TradingDataCapable<?>)Proxy.newProxyInstance(EconomySmoke.class.getClassLoader(),new Class[]{TradingDataCapable.class},(proxy,method,args)->switch(method.getName()){
    case "getSynchedEntityData"->state.get(args[0]);case "setSynchedEntityData"->{state.put((SynchedDataIndex)args[0],args[1]);yield null;}case "getMerchantTradingOffers"->runtime[0];case "setMerchantTradingOffers"->{runtime[0]=(MerchantOffers)args[0];yield null;}case "toString"->"synthetic NPC data proxy";case "hashCode"->System.identityHashCode(proxy);case "equals"->proxy==args[0];default->{if(method.isDefault())yield InvocationHandler.invokeDefault(proxy,method,args);throw new UnsupportedOperationException(method.toString());}
   });
   var saved=TagValueOutput.createWithContext(ProblemReporter.DISCARDING,lookup);npc.addAdditionalTradingData(saved);var tag=saved.buildResult();state.clear();npc.readAdditionalTradingData(TagValueInput.create(ProblemReporter.DISCARDING,lookup,tag));var restored=npc.getOffers();var ops=RegistryOps.create(NbtOps.INSTANCE,lookup);check(MerchantOffers.CODEC.encodeStart(ops,restored).getOrThrow().equals(MerchantOffers.CODEC.encodeStart(ops,spawn.offers()).getOrThrow()),"native NPC offer identity round trip");
   for(String member:family.members())check(restored.getFirst().satisfiedBy(new ItemStack(BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(member)),5),ItemStack.EMPTY),"family lost through native sanitizer/save/load");
   var buffer=new net.minecraft.network.RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),net.minecraft.core.RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));MerchantOffers.STREAM_CODEC.encode(buffer,restored);var network=MerchantOffers.STREAM_CODEC.decode(buffer);buffer.release();for(String member:family.members())check(network.getFirst().satisfiedBy(new ItemStack(BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(member)),5),ItemStack.EMPTY),"family lost in merchant network codec");
   var tool=EconomyItems.stack("minecraft:wooden_shovel",1,new com.google.gson.JsonObject(),lookup);check(tool.getMaxStackSize()==1,"real nonstackable limit");check(BarterSolver.solve(new BarterSolver.Good("out",24,1),List.of(new BarterSolver.Good("tool",8,tool.getMaxStackSize()))).isEmpty(),"three nonstackable tools illegally fit");
   com.mojang.logging.LogUtils.getLogger().info("[HJ Core] NPC_ECONOMY_SMOKE_OK: synthetic catalog, native family matching, sanitizer, NPC trade save/load identity, components, stock, native xp=10 and nonstackable limits; no NPC/world created");
  }catch(Throwable error){throw new IllegalStateException("Economy native data checks failed",error);}});}
 }));}
 private EconomySmoke(){}
}
