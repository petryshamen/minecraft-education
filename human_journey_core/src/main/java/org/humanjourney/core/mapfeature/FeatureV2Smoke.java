package org.humanjourney.core.mapfeature;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.data.*;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import org.humanjourney.core.water.WaterGeography.Point;
public final class FeatureV2Smoke {
 private static void check(boolean ok,String why){if(!ok)throw new IllegalStateException("Geometry v2: "+why);}
 public static void register(BusGroup group){GatherDataEvent.getBus(group).addListener(e->e.getGenerator().addProvider(true,new DataProvider(){public String getName(){return "Human Journey geometry v2 fragmented native codec check";}public java.util.concurrent.CompletableFuture<?> run(CachedOutput output){return java.util.concurrent.CompletableFuture.runAsync(FeatureV2Smoke::verify);}}));}
 private static void verify(){var parts=new ArrayList<FeatureGeometry.Part>();for(int i=0;i<512;i++){var points=new ArrayList<Point>();for(int j=0;j<32;j++){double a=2*Math.PI*j/32;points.add(new Point(i*100+10*Math.cos(a),10*Math.sin(a)));}parts.add(new FeatureGeometry.Part(new FeatureGeometry.Ring(points),List.of()));}var geometry=FeatureGeometry.multipolygon(parts);var feature=new MapFeature.View("v2_synthetic_parts","Synthetic Parts",MapFeature.Type.SEA,"minecraft:overworld",1,geometry);var frames=FeaturePackets.frames(new FeaturePackets.Header(UUID.randomUUID(),1,1,1),FeaturePackets.Mode.FULL,0,List.of(feature),List.of());check(geometry.vertexCount()==16384&&geometry.ringCount()==512,"minimum scale capability");check(frames.size()>1&&frames.stream().anyMatch(f->!f.fragments().isEmpty()),"large feature fragmented");var mirror=new ClientFeatureState();for(int i=frames.size()-1;i>=0;i--){var buffer=new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));try{FeaturePackets.CODEC.encode(buffer,frames.get(i));check(buffer.readableBytes()<=FeaturePackets.MAX_BYTES,"bounded native packet");mirror.accept(FeaturePackets.CODEC.decode(buffer));}finally{buffer.release();}}check(mirror.snapshot().features().get(feature.id()).equals(feature),"fragmented native geometry round trip");check(mirror.snapshot().index().queryParts("minecraft:overworld",FeatureIndex.around(50,0,1)).isEmpty(),"no combined giant feature bounding candidate");check(mirror.snapshot().index().hover("minecraft:overworld",0,0,5).partIndex()==0,"part identity");com.mojang.logging.LogUtils.getLogger().info("[HJ Core] MAP_GEOMETRY_V2_SMOKE_OK: 512-part/16384-vertex multipart feature, fragmented bounded native codec/atomic mirror, component BVH and hover; no world/player/client created");}
 private FeatureV2Smoke(){}
}
