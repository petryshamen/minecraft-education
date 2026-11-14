package org.humanjourney.core.geography.map;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.data.*;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import org.humanjourney.core.geography.*;
import org.humanjourney.core.npc.context.NpcGeography;
import org.humanjourney.core.water.*;
public final class MapSmoke {
 private static void check(boolean c,String s){if(!c)throw new IllegalStateException("Map geometry smoke: "+s);}
 private static NpcGeography.Area area(String id,String label,String parent,int priority,double size){var points=List.of(new WaterGeography.Point(0,0),new WaterGeography.Point(size,0),new WaterGeography.Point(size,size),new WaterGeography.Point(0,size));return new NpcGeography.Area(id,"minecraft:overworld",priority,parent,label,Map.of(),new WaterGeography.Region(id,"minecraft:overworld",WaterPolicy.Quality.NORMAL,points),null);}
 public static void register(BusGroup group){GatherDataEvent.getBus(group).addListener(e->e.getGenerator().addProvider(true,new DataProvider(){public String getName(){return "Human Journey discovered map geometry native checks";}public java.util.concurrent.CompletableFuture<?> run(CachedOutput out){return java.util.concurrent.CompletableFuture.runAsync(MapSmoke::verify);}}));}
 private static void verify(){try{var geo=new NpcGeography(List.of(area("macro","Known Macro","none",0,100),area("secret_macro","UNDISCOVERED_SECRET_LABEL","none",1,200)),List.of(area("local","Known Local","macro",0,20),area("secret_local","UNDISCOVERED_SECRET_LABEL","secret_macro",10,50)));var ledger=new DiscoveryLedger();ledger.discover(new GeographyRoles(true,true,false),new GeographyContext("macro","Known Macro","local","Known Local"));var view=new MapProjection.Factory(geo,"unknown").project(ledger.snapshot());check(view.areas().size()==2,"discovered only");var client=new ClientMapState();var header=new MapPackets.Header(UUID.randomUUID(),1,ledger.snapshot().revision(),1,1);for(var frame:MapPackets.frames(header,MapPackets.Mode.FULL,0,view.areas().values(),List.of())){var buffer=new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));try{MapPackets.CODEC.encode(buffer,frame);check(buffer.readableBytes()<=MapPackets.MAX_BYTES,"native packet byte bounds");check(!buffer.toString(0,buffer.writerIndex(),java.nio.charset.StandardCharsets.UTF_8).contains("UNDISCOVERED_SECRET_LABEL"),"hidden labels never serialized");client.accept(MapPackets.CODEC.decode(buffer));}finally{buffer.release();}}check(client.snapshot().areas().equals(view.areas()),"native geometry packet round trip");var hover=client.snapshot().index().hover("minecraft:overworld",10,10);check(hover.primary().equals("Known Local")&&hover.secondary().equals("Known Macro"),"discovered hover/parent");check(MapSmoke.class.getClassLoader().getResource("xaero/map/gui/GuiMap.class")==null,"Xaero absent from dedicated/data runtime");Class.forName("org.humanjourney.core.integration.xaero.XaeroMixinPlugin");Class.forName("org.humanjourney.core.geography.map.MapServer");com.mojang.logging.LogUtils.getLogger().info("[HJ Core] MAP_GEOMETRY_SMOKE_OK: discovered-only native packet round trip/privacy, hover, bounded transfer and common class loading with Xaero absent; no world/player/client created");}catch(Exception error){throw new IllegalStateException("Map native checks failed",error);}}
 private MapSmoke(){}
}
