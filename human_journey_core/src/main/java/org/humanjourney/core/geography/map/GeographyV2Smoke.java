package org.humanjourney.core.geography.map;
import java.util.*;
import org.humanjourney.core.mapfeature.FeatureGeometry;
import org.humanjourney.core.npc.context.NpcGeography;
import org.humanjourney.core.water.WaterGeography.Point;
import org.humanjourney.core.geography.DiscoveryLedger;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.data.*;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
public final class GeographyV2Smoke {
 public static void register(BusGroup group){GatherDataEvent.getBus(group).addListener(e->e.getGenerator().addProvider(true,new DataProvider(){public String getName(){return "Human Journey geography v2 native fragmented geometry";}public java.util.concurrent.CompletableFuture<?> run(CachedOutput output){return java.util.concurrent.CompletableFuture.runAsync(GeographyV2Smoke::verify);}}));}
 private static void check(boolean b,String why){if(!b)throw new IllegalStateException("geography v2: "+why);}
 private static void verify(){var parts=new ArrayList<FeatureGeometry.Part>();for(int i=0;i<512;i++){var points=new ArrayList<Point>();for(int j=0;j<32;j++){double angle=j*2*Math.PI/32;points.add(new Point(i*300+100*Math.cos(angle),100*Math.sin(angle)));}parts.add(new FeatureGeometry.Part(new FeatureGeometry.Ring(points),List.of()));}var geometry=FeatureGeometry.multipolygon(parts);var area=new NpcGeography.Area("synthetic_local","minecraft:overworld",0,"none","Synthetic Local",Map.of(),geometry,null);var geo=new NpcGeography(List.of(),List.of(area));check(geo.resolve(area.dimension(),300,0,"era_0").local().equals(area.id()),"authoritative indexed resolution");check(geo.candidateCount(area.dimension(),300,0)==1,"component candidate count");var known=new MapProjection.Factory(geo,"era_0").project(new DiscoveryLedger.Snapshot(Set.of(),Set.of(area.id()),1));var mirror=new ClientMapState();var frames=MapPackets.frames(new MapPackets.Header(UUID.randomUUID(),1,1,1,1),MapPackets.Mode.FULL,0,known.areas().values(),List.of());check(frames.size()==17,"maximum area fragmented");for(int i=frames.size()-1;i>=0;i--){var b=new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));try{MapPackets.CODEC.encode(b,frames.get(i));check(b.readableBytes()<=48*1024,"packet cap");mirror.accept(MapPackets.CODEC.decode(b));if(i>0)check(mirror.snapshot().areas().isEmpty(),"atomic publication");}finally{b.release();}}check(mirror.snapshot().index().hover(area.dimension(),300,0).localComponent()==1,"native component identity");com.mojang.logging.LogUtils.getLogger().info("[HJ Core] GEOGRAPHY_V2_SMOKE_OK: 512parts/16384vertices, authoritative component index, discovered projection, bounded native fragments/atomic mirror; no world/player/client created");}
 private GeographyV2Smoke(){}
}
