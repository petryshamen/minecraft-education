package org.humanjourney.core.progress;
import java.util.*;
import com.google.gson.JsonParser;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.data.*;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
/** Native codec/common loading only; does not construct a world, player, Screen or GL context. */
public final class ProgressSmoke {
 public static void register(BusGroup group){GatherDataEvent.getBus(group).addListener(e->e.getGenerator().addProvider(true,new DataProvider(){public String getName(){return "Human Journey read-only civilization graph native checks";}public java.util.concurrent.CompletableFuture<?> run(CachedOutput output){return java.util.concurrent.CompletableFuture.runAsync(ProgressSmoke::verify);}}));}
 private static void verify(){try{String text=java.nio.file.Files.readString(java.nio.file.Path.of("../../example-civilization-progress-datapack/data/human_journey/core/civilization_progress.json"));var graph=ProgressGraph.parse(JsonParser.parseString(text),id->BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(id)));var view=ProgressView.project(graph,(o,h)->OptionalInt.of(0));var mirror=new ProgressMirror();for(var frame:ProgressPackets.frames(UUID.randomUUID(),1,view)){var b=new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));try{ProgressPackets.CODEC.encode(b,frame);if(b.readableBytes()>ProgressPackets.MAX_PACKET)throw new IllegalStateException("packet cap");if(!mirror.accept(ProgressPackets.CODEC.decode(b)))throw new IllegalStateException("native frame rejected");}finally{b.release();}}if(!view.equals(mirror.view()))throw new IllegalStateException("native snapshot mismatch");Class.forName("org.humanjourney.core.progress.ProgressBootstrap");Class.forName("org.humanjourney.core.progress.ProgressServer");com.mojang.logging.LogUtils.getLogger().info("[HJ Core] CIVILIZATION_PROGRESS_SMOKE_OK:18-node example, native bounded codec/atomic mirror, shared score reads and common headless loading; no world/player/Screen constructed");}catch(Exception e){throw new IllegalStateException("Civilization Progress native check failed",e);}}
 private ProgressSmoke(){}
}
