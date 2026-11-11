package org.humanjourney.core.geography;
import java.util.*;
import java.nio.file.*;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.*;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
/** Native save/packet checks under Forge without constructing a player, world, client or server gameplay session. */
public final class GeographySmoke {
 static void check(boolean condition,String message){if(!condition)throw new IllegalStateException(message);}
 public static void register(BusGroup group){GatherDataEvent.getBus(group).addListener(event->event.getGenerator().addProvider(true,new DataProvider(){
  public String getName(){return "Human Journey shared geography native persistence/network checks";}
  public CompletableFuture<?> run(CachedOutput output){return CompletableFuture.runAsync(()->{try{
   var saved=new SavedDiscovery();var view=new GeographyContext("test_macro","Test Macro","test_local","Test Local");saved.discover(new GeographyRoles(true,true,false),view);saved.mark(DiscoveryLedger.Kind.LOCAL,"removed_test_area");
   var tag=(CompoundTag)SavedDiscovery.CODEC.encodeStart(NbtOps.INSTANCE,saved).getOrThrow();var path=Path.of("geography-native-check.dat");NbtIo.writeCompressed(tag,path);var restored=SavedDiscovery.CODEC.parse(NbtOps.INSTANCE,NbtIo.readCompressed(path,NbtAccounter.unlimitedHeap())).getOrThrow();Files.delete(path);check(restored.snapshot().equals(saved.snapshot()),"shared discovery compressed NBT round trip");
   var mirror=new ClientGeographyState();for(var packet:GeographyPackets.snapshot(restored.snapshot())){var buffer=new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));GeographyPackets.DISCOVERY.encode(buffer,packet);mirror.accept(GeographyPackets.DISCOVERY.decode(buffer));buffer.release();}check(mirror.discovery().equals(restored.snapshot()),"native discovery packet mirror");
   var buffer=new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));GeographyPackets.CONTEXT.encode(buffer,new GeographyPackets.Context(true,view));mirror.accept(GeographyPackets.CONTEXT.decode(buffer));buffer.release();check(mirror.hud().primary().equals("Test Local")&&mirror.hud().secondary().equals("Test Macro"),"label-only HUD model");
   Class.forName("org.humanjourney.core.geography.GeographyBootstrap");Class.forName("org.humanjourney.core.geography.GeographyServer");
   com.mojang.logging.LogUtils.getLogger().info("[HJ Core] GEOGRAPHY_DISCOVERY_SMOKE_OK: native SavedData compressed NBT, context/discovery codecs, shared IDs/stale retention and headless common class loading; no player/world/client created");
  }catch(Exception e){throw new IllegalStateException("Geography native checks failed",e);}});}
 }));}
 private GeographySmoke(){}
}
