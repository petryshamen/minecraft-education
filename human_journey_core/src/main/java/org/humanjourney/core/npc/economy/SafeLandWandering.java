package org.humanjourney.core.npc.economy;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.pathfinder.*;
/** Conservative native land checks; does not load chunks or restrict recovery paths. */
public final class SafeLandWandering {
 public static boolean landAt(PathfinderMob mob,BlockPos pos){
  var level=mob.level();if(!level.hasChunkAt(pos)||!level.hasChunkAt(pos.below())||pos.getY()<level.getMinY()+1||pos.getY()+mob.getBbHeight()>=level.getMaxY())return false;
  var box=mob.getBoundingBox().move(pos.getX()+.5-mob.getX(),pos.getY()-mob.getY(),pos.getZ()+.5-mob.getZ());boolean fluid=false;
  for(int x=Mth.floor(box.minX)-1;x<=Mth.floor(box.maxX)+1;x++)for(int z=Mth.floor(box.minZ)-1;z<=Mth.floor(box.maxZ)+1;z++)if(!level.hasChunkAt(new BlockPos(x,pos.getY(),z)))return false;
  for(var cell:BlockPos.betweenClosed(Mth.floor(box.minX),Mth.floor(box.minY),Mth.floor(box.minZ),Mth.floor(box.maxX-1e-7),Mth.floor(box.maxY-1e-7),Mth.floor(box.maxZ-1e-7))){if(!level.hasChunkAt(cell))return false;if(!level.getFluidState(cell).isEmpty())fluid=true;}
  if(!level.getFluidState(pos.below()).isEmpty())fluid=true;
  var floor=level.getBlockState(pos.below());boolean support=floor.entityCanStandOn(level,pos.below(),mob);boolean clear=level.noCollision(mob,box);
  var type=WalkNodeEvaluator.getPathTypeStatic(mob,pos);boolean harmless=safeType(type);
  return WanderSafety.land(fluid,support,clear,harmless);
 }
 public static boolean safeType(PathType type){return switch(type){case OPEN,WALKABLE,WALKABLE_DOOR,DOOR_OPEN,RAIL,WATER_BORDER->true;default->false;};}
 public static boolean path(PathfinderMob mob,Path path,BlockPos home,int radius,boolean remaining){
  if(path==null)return false;var nodes=new ArrayList<WanderSafety.Point>();int begin=remaining?path.getNextNodeIndex():0;
  for(int i=begin;i<path.getNodeCount();i++){var node=path.getNode(i);if(!safeType(node.type))return false;nodes.add(new WanderSafety.Point(node.x,node.y,node.z));}
  // Revalidate planned walkable positions, not the transient floored feet cell during a native jump.
  var start=mob.blockPosition();if(remaining&&path.getNodeCount()>0){var previous=path.getNode(Math.max(0,begin-1));start=new BlockPos(previous.x,previous.y,previous.z);}return WanderSafety.route(new WanderSafety.Point(start.getX(),start.getY(),start.getZ()),nodes,p->EconomyMovement.inside(home.getX()+.5,home.getY(),home.getZ()+.5,p.x()+.5,p.y(),p.z()+.5,radius)&&landAt(mob,new BlockPos(p.x(),p.y(),p.z())));
 }
 private SafeLandWandering(){}
}
