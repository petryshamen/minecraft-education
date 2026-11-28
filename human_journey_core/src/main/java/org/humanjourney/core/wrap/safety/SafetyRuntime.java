package org.humanjourney.core.wrap.safety;
import java.util.*;
import net.minecraft.server.*;
import net.minecraft.server.level.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.phys.*;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import org.humanjourney.core.wrap.*;

/** No world/entity recreation. Pending jobs freeze outward travel, not survival state. */
public final class SafetyRuntime {
 private static final org.slf4j.Logger LOG=com.mojang.logging.LogUtils.getLogger();
 private static final Map<Entity,Job> JOBS=new IdentityHashMap<>();
 private static final Map<Entity,Anchor> ANCHORS=new WeakHashMap<>();
 private static final Map<Entity,Long> WARNED=new WeakHashMap<>();
 private static String last="HELD | crossed=false | no attempt";
 private record Anchor(List<Entity> group,List<Entity> vehicles,List<PositionMoveRotation> poses){}
 private static final class Job {
  final Entity root;final ServerLevel level;final List<Entity> group,vehicles;final Map<UUID,Vec3> motions=new HashMap<>();final double sourceX,target,canonical;final Vec3 source;final long deadline;final ExistingDestination destination;final boolean recovery;
  Job(Entity root,List<Entity> group,ServerLevel level,double canonical,double target,boolean recovery){this.root=root;this.group=group;this.level=level;this.canonical=canonical;this.target=target;source=root.position();sourceX=root.getX();this.recovery=recovery;vehicles=group.stream().map(Entity::getVehicle).toList();group.forEach(e->motions.put(e.getUUID(),e.getKnownMovement()));deadline=level.getGameTime()+200;destination=new ExistingDestination(level,group.stream().map(e->e.getBoundingBox().move(target-sourceX,0,0)).toList());}
 }
 public static List<Entity> group(Entity root){var all=new ArrayList<Entity>();var pending=new ArrayDeque<Entity>();pending.push(root);while(!pending.isEmpty()){var e=pending.pop();if(all.size()>=16||all.contains(e))throw new IllegalArgumentException("unsupported/cyclic/>16-member hierarchy");all.add(e);for(var p:e.getPassengers())pending.push(p);}return List.copyOf(all);}
 private static double[] limits(Entity root,List<Entity> group){double minX=0,maxX=0,minZ=0,maxZ=0;for(var e:group){var b=e.getBoundingBox();minX=Math.min(minX,b.minX-root.getX());maxX=Math.max(maxX,b.maxX-root.getX());minZ=Math.min(minZ,b.minZ-root.getZ());maxZ=Math.max(maxZ,b.maxZ-root.getZ());}int pad=SafetyPolicy.BUFFER+1;return new double[]{-12288+pad-minX,12288-pad-maxX,-6144+pad-minZ,6144-pad-maxZ};}
 private static boolean contained(List<Entity> group){return group.stream().allMatch(e->{var b=e.getBoundingBox();return SafetyPolicy.readable(b.minX,b.minZ)&&SafetyPolicy.readable(b.maxX,b.maxZ);});}
 public static boolean rejectPosition(Entity entity,double x,double z){if(!(entity.level() instanceof ServerLevel level)||!EarthProtection.active(level)||!entity.isAddedToWorld()||SafetyPolicy.readable(x,z))return false;state("HELD",entity.getRootVehicle(),"direct position outside protected readable band rejected",entity.getX(),false);return true;}
 private static void state(String value,Entity root,String reason,double target,boolean crossed){last=value+" | crossed="+crossed+" | group="+root.getUUID()+" actual="+root.position()+" target="+new Vec3(target,root.getY(),root.getZ())+" | "+reason;long now=root.level().getGameTime();if(crossed||now-WARNED.getOrDefault(root,-1000L)>=200){WARNED.put(root,now);LOG.info("[HJ Earth] {}",last);}}
 public static String status(){return EarthProtection.describe()+" wrapping_requested="+WrapConfig.ENABLED.get()+" wrapping_active="+(WrapConfig.ENABLED.get()&&EarthProtection.ready())+" jobs="+JOBS.size()+" tickets="+ExistingDestination.ticketCount()+" | "+last+" | Xaero visual wrap: not implemented";}
 private static void request(Entity root,double attempted){
  var level=(ServerLevel)root.level();if(JOBS.containsKey(root))return;
  List<Entity> entities;try{entities=group(root);}catch(RuntimeException e){state("FAILED",root,e.getMessage(),root.getX(),false);return;}
  boolean recovery=!contained(entities);
  if(!recovery&&(!WrapConfig.ENABLED.get()||!EarthProtection.ready())){state("HELD",root,"wrapping disabled or protection not ready",root.getX(),false);return;}
  if(JOBS.size()>=4){state("HELD",root,"bounded destination queue full",root.getX(),false);return;}
  if(!SafetyPolicy.readable(0,root.getZ())){state("FAILED",root,"latitude outside protected Earth; no north/south wrapping",root.getX(),false);if(recovery)stop(level.getServer(),root,"no verified north/south recovery position");return;}
  double canonical;
  if(recovery){var safe=limits(root,entities);canonical=SafetyPolicy.clamp(root.getX(),safe[0],safe[1]);}
  else if(WrapMath.outside(attempted))canonical=WrapMath.canonical(attempted);
  else{var lim=limits(root,entities);boolean east=attempted>lim[1];double overshoot=Math.min(64,Math.max(0,east?attempted-lim[1]:lim[0]-attempted));canonical=east?-12288+overshoot:Math.nextDown(12288.0)-overshoot;}
  double target=canonical;int inset=WrapConfig.ARRIVAL_INSET.get();if(inset>0)target=SafetyPolicy.clamp(canonical,-12288+inset,12288-inset);
  var job=new Job(root,entities,level,canonical,target,recovery);JOBS.put(root,job);state("LOADING",root,"existing-only destination preparation; canonicalX="+canonical+" adjustment="+(target-canonical),target,false);
 }
 public static Vec3 limit(Entity entity,Vec3 motion){
  if(!(entity.level() instanceof ServerLevel level)||!EarthProtection.active(level)||entity.getRootVehicle()!=entity)return motion;
  List<Entity> all;try{all=group(entity);}catch(RuntimeException e){if(entity instanceof ServerPlayer)return Vec3.ZERO;for(var passenger:entity.getIndirectPassengers())if(passenger instanceof ServerPlayer)return Vec3.ZERO;return motion;}
  if(all.stream().noneMatch(e->e instanceof ServerPlayer)){
   if(!contained(all))return Vec3.ZERO;var bound=limits(entity,all);return new Vec3(SafetyPolicy.clamp(entity.getX()+motion.x,bound[0],bound[1])-entity.getX(),motion.y,SafetyPolicy.clamp(entity.getZ()+motion.z,bound[2],bound[3])-entity.getZ());
  }
  if(!Double.isFinite(motion.x)||!Double.isFinite(motion.z))return motion;
  var lim=limits(entity,all);
  if(!contained(all)){request(entity,entity.getX());return Vec3.ZERO;}
  double x=SafetyPolicy.clamp(entity.getX()+motion.x,lim[0],lim[1]),z=SafetyPolicy.clamp(entity.getZ()+motion.z,lim[2],lim[3]);
  if(x!=entity.getX()+motion.x)request(entity,entity.getX()+motion.x);
  if(z!=entity.getZ()+motion.z)state("HELD",entity,"north/south boundary; no latitude wrapping",entity.getX(),false);
  return new Vec3(x-entity.getX(),motion.y,z-entity.getZ());
 }
 public static boolean packet(ServerPlayer player,double proposedX,double proposedZ){
  var level=player.level();if(!EarthProtection.active(level)||!Double.isFinite(proposedX)||!Double.isFinite(proposedZ))return false;
  var root=player.getRootVehicle();List<Entity> all;try{all=group(root);}catch(RuntimeException e){state("FAILED",root,e.getMessage(),root.getX(),false);return true;}
  var lim=limits(root,all);boolean outside=proposedX<lim[0]||proposedX>lim[1]||proposedZ<lim[2]||proposedZ>lim[3]||!contained(all);
  if(!outside)return false;
  boolean nearSeam=root.getX()<lim[0]+4||root.getX()>lim[1]-4||!contained(all);
  if(nearSeam&&Math.abs(proposedX-root.getX())<=64&&Math.abs(proposedZ-root.getZ())<=64&&(proposedX<lim[0]||proposedX>lim[1]||!contained(all)))request(root,proposedX);
  else state("HELD",root,"invalid/too-fast or north/south boundary proposal",root.getX(),false);
  correct(root,all);return true;
 }
 private static void correct(Entity root,List<Entity> all){
  var velocity=root.getDeltaMovement();root.setDeltaMovement(new Vec3(0,velocity.y,velocity.z));
  for(var e:all)if(e instanceof ServerPlayer p&&p.connection instanceof WrapConnection c&&!c.hjWrap$pending()){
   p.connection.teleport(new PositionMoveRotation(p.position(),new Vec3(0,p.getKnownMovement().y,p.getKnownMovement().z),p.getYRot(),p.getXRot()),Set.of());p.connection.resetPosition();
   if(root!=p){p.connection.send(ClientboundTeleportEntityPacket.teleport(root.getId(),new PositionMoveRotation(root.position(),root.getDeltaMovement(),root.getYRot(),root.getXRot()),Set.of(),root.onGround()));c.hjWrap$resetVehicle(root);}
  }
 }
 public static void tick(MinecraftServer server){
  if(!EarthProtection.ready())return;
  var seen=Collections.newSetFromMap(new IdentityHashMap<Entity,Boolean>());
  for(var player:server.getPlayerList().getPlayers())if(EarthProtection.active(player.level())&&player.isAlive()){
   var root=player.getRootVehicle();if(!seen.add(root))continue;
   try{var all=group(root);if(contained(all))ANCHORS.put(root,new Anchor(all,all.stream().map(Entity::getVehicle).toList(),all.stream().map(e->new PositionMoveRotation(e.position(),e.getKnownMovement(),e.getYRot(),e.getXRot())).toList()));else{
    var anchor=ANCHORS.get(root);
    if(anchor!=null&&anchor.group.equals(all)&&anchor.vehicles.equals(all.stream().map(Entity::getVehicle).toList())&&restore(anchor,(ServerLevel)root.level())){state("HELD",root,"whole group restored to validated inside anchor",root.getX(),false);}
    else{request(root,root.getX());correct(root,all);}
   }}catch(RuntimeException e){stop(server,root,"containment failure: "+e.getMessage());return;}
  }
  for(var job:List.copyOf(JOBS.values())){
   List<Entity> current;try{current=group(job.root);}catch(RuntimeException changed){close(job,"FAILED",changed.getMessage());continue;}
   if(job.root.isRemoved()||job.group.stream().anyMatch(e->!e.isAlive()||e.isRemoved())||!job.group.equals(current)||!job.vehicles.equals(job.group.stream().map(Entity::getVehicle).toList())){close(job,"FAILED","riding group changed; no partial transfer");continue;}
   // Retreat/movement invalidates the prepared translation; the next attempt uses a fresh footprint.
   if(Math.abs(job.root.getX()-job.sourceX)>1||Math.abs(job.root.getZ()-job.source.z)>1||Math.abs(job.root.getY()-job.source.y)>1){close(job,"HELD","source moved; preparation cancelled");continue;}
   if(job.destination.failed()||job.level.getGameTime()>=job.deadline){String why=job.destination.failed()?job.destination.failure:"destination loading timeout";close(job,"FAILED",why);if(job.recovery&&!contained(job.group)){stop(server,job.root,"no validated inside recovery: "+why);return;}continue;}
   if(!job.destination.ready())continue;
   var members=job.group.stream().map(e->new WrapRuntime.Member(e,job.root,job.level,job.motions.get(e.getUUID()),job.motions.get(job.root.getUUID()))).toList();
   var crossingSource=job.root.position();var result=WrapTransaction.run(members,job.target-job.root.getX());
   if(result.moved()){double actual=job.root.getX();close(job,job.recovery?"HELD":"CROSSED","committed=true recovered="+job.recovery+" members="+members.size()+" source="+crossingSource+" canonicalX="+job.canonical+" actualX="+actual+" inward_adjustment="+(actual-job.canonical));}
   else if(!result.reason().contains("acknowledgement")){close(job,"FAILED",result.reason());if(job.recovery&&!contained(job.group)){stop(server,job.root,"recovery placement failed: "+result.reason());return;}}
  }
 }
 private static boolean restore(Anchor a,ServerLevel level){
  for(int i=0;i<a.group.size();i++){var e=a.group.get(i);var target=a.poses.get(i).position();var box=e.getBoundingBox().move(target.subtract(e.position()));if(!SafetyPolicy.inside(box.minX,box.maxX,box.minZ,box.maxZ)||!level.hasChunkAt(net.minecraft.core.BlockPos.containing(target))||!level.noCollision(e,box.deflate(.001)))return false;}
  var moves=new ArrayList<WrapTransaction.Member>();
  for(int i=0;i<a.group.size();i++){var e=a.group.get(i);var original=new PositionMoveRotation(e.position(),e.getKnownMovement(),e.getYRot(),e.getXRot());var target=new PositionMoveRotation(a.poses.get(i).position(),Vec3.ZERO,e.getYRot(),e.getXRot());moves.add(new WrapTransaction.Member(){public String problem(double ignored){return null;}public void move(double ignored){e.teleportSetPosition(target,Set.of());}public void restore(){e.teleportSetPosition(original,Set.of());}public void publish(){}});}
  if(!WrapTransaction.run(moves,1).moved())return false;correct(a.group.get(0),a.group);return true;
 }
 private static void close(Job job,String value,String reason){job.destination.close();JOBS.remove(job.root);state(value,job.root,reason,job.target,value.equals("CROSSED"));}
 private static void stop(MinecraftServer server,Entity root,String reason){state("FAILED",root,reason+"; stopping server to prevent unsafe outside exploration",root.getX(),false);closeAll();server.halt(false);}
 public static void closeAll(){for(var job:List.copyOf(JOBS.values()))job.destination.close();JOBS.clear();ExistingDestination.clear();ANCHORS.clear();WARNED.clear();}
 private SafetyRuntime(){}
}
