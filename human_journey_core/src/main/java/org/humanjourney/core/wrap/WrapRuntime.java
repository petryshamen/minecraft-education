package org.humanjourney.core.wrap;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.world.phys.*;

/** Tick-thread only. Loaded-destination prototype: no chunk requests, tickets or terrain writes. */
public final class WrapRuntime {
 private static final org.slf4j.Logger LOG=com.mojang.logging.LogUtils.getLogger();
 private static final Map<Entity,Long> WARNED=new WeakHashMap<>();
 private static String last="not attempted";
 private static boolean broken;
 public static void clear(){WARNED.clear();last="not attempted";broken=false;}
 public static String status(){return "Physical wrap: "+(WrapConfig.ENABLED.get()?"enabled":"disabled")+" | exact runtime="+WrapMixinPlugin.supported()+" | interval [-12288,12288), width 24576 | Overworld Z [-6144,6144] | loaded destinations only | Xaero visual wrap: not implemented | last: "+last+(broken?" | stopped after runtime failure":"");}
 public static void tick(MinecraftServer server){
  if(!WrapConfig.ENABLED.get()||!WrapMixinPlugin.supported()||broken)return;
  var seen=Collections.newSetFromMap(new IdentityHashMap<Entity,Boolean>());
  for(var player:server.getPlayerList().getPlayers()){
   if(player.level().dimension()!=Level.OVERWORLD||!player.isAlive()||player.isSleeping())continue;
   var root=player.getRootVehicle();if(!seen.add(root)||!WrapMath.outside(root.getX()))continue;
   try{cross(root,player.level());}catch(RuntimeException e){broken=true;LOG.error("[HJ wrap] Stopped world wrap after unexpected failure. Inspect group {} before re-enabling/restarting.",root.getUUID(),e);return;}
  }
 }
 static void cross(Entity root,ServerLevel level){
  if(Math.abs(root.getZ())>6144){defer(root,level,"outside valid Earth latitude; no north/south wrap");return;}
  var entities=new ArrayList<Entity>();var stack=new ArrayDeque<Entity>();stack.push(root);
  while(!stack.isEmpty()){
   var entity=stack.pop();if(entities.size()>=16||entities.contains(entity)){defer(root,level,"unsupported/cyclic or >16-member riding group");return;}entities.add(entity);
   for(var child:entity.getPassengers())stack.push(child);
  }
  if(!(root instanceof ServerPlayer||root instanceof AbstractBoat||root instanceof AbstractHorse)){defer(root,level,"unsupported root vehicle: "+root.getType());return;}
  double dx=WrapMath.canonical(root.getX())-root.getX();
  var members=entities.stream().map(entity->new Member(entity,root,level)).toList();
  var result=WrapTransaction.run(members,dx);
  if(!result.moved()){defer(root,level,result.reason());return;}
  last="crossed "+(dx<0?"east → west":"west → east")+"; members="+members.size()+" X="+(root.getX()-dx)+" → "+root.getX()+" Y="+root.getY()+" Z="+root.getZ();
  WARNED.remove(root);if(WrapConfig.LOG_CROSSINGS.get())LOG.info("[HJ wrap] {} group={}",last,root.getUUID());
 }
 private static void defer(Entity root,ServerLevel level,String reason){
  last="deferred group="+root.getUUID()+" X/Y/Z="+root.position()+": "+reason;
  long now=level.getGameTime();Long before=WARNED.get(root);
  if(before==null||now-before>=200){WARNED.put(root,now);LOG.warn("[HJ wrap] {} (no member moved)",last);}
 }
 // Actual authoritative server coordinates, with native known movement for controlled vehicles.
 static PositionMoveRotation snapshot(Entity e){return new PositionMoveRotation(e.position(),e.getKnownMovement(),e.getYRot(),e.getXRot());}
 static final class Member implements WrapTransaction.Member {
  final Entity entity,root;final ServerLevel level;final PositionMoveRotation before;final Entity vehicle;final List<Entity> passengers;
  Member(Entity entity,Entity root,ServerLevel level){this.entity=entity;this.root=root;this.level=level;this.before=snapshot(entity);vehicle=entity.getVehicle();passengers=List.copyOf(entity.getPassengers());}
  public String problem(double dx){
   if(!entity.isAlive()||entity.isRemoved()||entity.level()!=level||entity.getVehicle()!=vehicle||!entity.getPassengers().equals(passengers))return "changed/dead riding group";
   if(!(entity instanceof ServerPlayer)&&!entity.getClass().getName().startsWith("net.minecraft.world.entity."))return "unsupported modded entity class";
   if(entity instanceof ServerPlayer p){
    if(p.isSleeping()||p.connection==null||!(p.connection instanceof WrapConnection c)||c.hjWrap$pending())return "player unavailable or teleport acknowledgement pending";
   }
   AABB box=entity.getBoundingBox().move(dx,0,0);
   if(!Double.isFinite(box.minX)||!Double.isFinite(box.minY)||!Double.isFinite(box.minZ)||box.getXsize()>8||box.getYsize()>8||box.getZsize()>8)return "invalid/oversized entity bounds";
   if(Math.abs(box.minZ)>6145||Math.abs(box.maxZ)>6145||box.minY<level.getMinY()||box.maxY>=level.getMaxY())return "destination outside world build/latitude limits";
   if(!level.getWorldBorder().isWithinBounds(box))return "destination outside current world border";
   // Include supporting blocks and a one-block collision-query margin. getChunkNow never requests terrain.
   for(int x=(int)Math.floor(box.minX-1)>>4;x<=((int)Math.floor(box.maxX+1)>>4);x++)for(int z=(int)Math.floor(box.minZ-1)>>4;z<=((int)Math.floor(box.maxZ+1)>>4);z++)
    if(level.getChunkSource().getChunkNow(x,z)==null)return "destination chunks not loaded; preload existing test-world chunks, then retry";
   if(!level.noCollision(entity,box.deflate(0.001)))return "destination collision";
   for(BlockPos p:BlockPos.betweenClosed(BlockPos.containing(box.minX,box.minY-0.1,box.minZ),BlockPos.containing(box.maxX,box.maxY,box.maxZ))){
    var fluid=level.getFluidState(p);var block=level.getBlockState(p);
    if(!fluid.isEmpty()&&!fluid.is(FluidTags.WATER))return "dangerous destination fluid";
    if(block.is(Blocks.FIRE)||block.is(Blocks.SOUL_FIRE)||block.is(Blocks.CACTUS)||block.is(Blocks.MAGMA_BLOCK)||block.is(Blocks.POWDER_SNOW)||block.is(Blocks.SWEET_BERRY_BUSH)||block.is(Blocks.WITHER_ROSE)||block.is(Blocks.NETHER_PORTAL)||block.is(Blocks.END_PORTAL)||block.is(Blocks.END_GATEWAY)||block.is(Blocks.CAMPFIRE)||block.is(Blocks.SOUL_CAMPFIRE))return "hazardous destination block";
   }
   if(entity==root){
    boolean water=true;
    for(double x:new double[]{box.minX+0.1,(box.minX+box.maxX)/2,box.maxX-0.1})for(double z:new double[]{box.minZ+0.1,(box.minZ+box.maxZ)/2,box.maxZ-0.1})
     water&=level.getFluidState(BlockPos.containing(x,box.minY-0.05,z)).is(FluidTags.WATER);
    if(entity instanceof AbstractBoat&&!water)return "boat destination needs water under its full footprint";
    if(entity instanceof AbstractBoat&&!level.getFluidState(BlockPos.containing((box.minX+box.maxX)/2,box.minY+0.6,(box.minZ+box.maxZ)/2)).isEmpty())return "boat destination is submerged";
    boolean flying=entity instanceof ServerPlayer p&&(p.getAbilities().flying||p.isFallFlying());
    if(!(entity instanceof AbstractBoat)&&!flying&&!water&&!level.getBlockCollisions(entity,box.move(0,-0.125,0)).iterator().hasNext())return "destination has no immediate ground/water support";
    if(entity instanceof AbstractHorse&&water)return "horse destination must be supported land";
   }
   return null;
  }
  PositionMoveRotation shifted(double dx){return new PositionMoveRotation(before.position().add(dx,0,0),before.deltaMovement(),before.yRot(),before.xRot());}
  public void move(double dx){entity.teleportSetPosition(shifted(dx),Set.of());}
  public void restore(){entity.teleportSetPosition(before,Set.of());}
  public void publish(){
   if(entity instanceof ServerPlayer p){
    // Same-level narrow primitive used by vanilla ServerPlayer.teleport: keeps mount links,
    // uses normal acknowledged player teleport and absolute movement/rotation, no travel hook.
    p.connection.teleport(snapshot(p),Set.of());p.connection.resetPosition();
    if(root!=p){p.connection.send(ClientboundTeleportEntityPacket.teleport(root.getId(),snapshot(root),Set.of(),root.onGround()));((WrapConnection)p.connection).hjWrap$resetVehicle(root);}
   }
  }
 }
 private WrapRuntime(){}
}
