package org.humanjourney.core.wrap;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.*;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;

/** Real Forge class transformations + native coordinate primitive, no live world or entity. */
public final class WrapSmoke {
 public static void register(BusGroup group){GatherDataEvent.getBus(group).addListener(event->event.getGenerator().addProvider(true,new DataProvider(){
  public String getName(){return "Human Journey world-wrap native primitive checks";}
  public CompletableFuture<?> run(CachedOutput output){
   try{
    if(!WrapConnection.class.isAssignableFrom(Class.forName("net.minecraft.server.network.ServerGamePacketListenerImpl")))throw new IllegalStateException("Wrap connection bridge missing");
    var old=new PositionMoveRotation(new Vec3(12288.25,63.75,-55.125),new Vec3(0.7,-0.1,0.2),137.5f,-12.5f);
    var delta=new PositionMoveRotation(new Vec3(-24576,0,0),Vec3.ZERO,0,0);
    var actual=PositionMoveRotation.calculateAbsolute(old,delta,net.minecraft.world.entity.Relative.ALL);
    if(actual.position().x!=-12287.75||actual.position().y!=63.75||actual.position().z!=-55.125||!actual.deltaMovement().equals(old.deltaMovement())||actual.yRot()!=137.5f||actual.xRot()!=-12.5f)throw new IllegalStateException("Native coordinate/velocity primitive mismatch");
    if(WrapConfig.ENABLED.getDefault())throw new IllegalStateException("Wrap must default disabled");
    com.mojang.logging.LogUtils.getLogger().info("[HJ Core] WORLD_WRAP_SMOKE_OK: actual common-side connection Mixin and native position/rotation/velocity primitive; no world, boat, horse, player, network session or gameplay test");
    return CompletableFuture.completedFuture(null);
   }catch(Exception e){return CompletableFuture.failedFuture(e);}
  }
 }));}
 private WrapSmoke(){}
}
