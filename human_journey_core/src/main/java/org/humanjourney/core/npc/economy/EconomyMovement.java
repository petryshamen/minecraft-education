package org.humanjourney.core.npc.economy;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPCBase;
import de.markusbordihn.easynpc.data.objective.ObjectiveDataSet;
/** Only marked dynamic NPCs. Fixed saved home; bounded target/path selection; native navigation and look controls. */
public final class EconomyMovement {
 public static final String KEY="human_journey:economy_v1";
 public interface Managed{}
 public static boolean inside(double hx,double hy,double hz,double x,double y,double z,int radius){return (x-hx)*(x-hx)+(y-hy)*(y-hy)+(z-hz)*(z-hz)<=radius*(double)radius;}
 public static void configure(PathfinderMob mob,EconomySpawn spawn){var home=mob.blockPosition();var tag=new CompoundTag();tag.putInt("x",home.getX());tag.putInt("y",home.getY());tag.putInt("z",home.getZ());tag.putInt("radius",spawn.settings().wanderRadius());tag.putBoolean("wander",spawn.settings().wandering());tag.putBoolean("look",spawn.settings().lookAtPlayers());tag.putString("era",spawn.context().era());tag.putString("macro",spawn.context().macroRegion());tag.putString("local",spawn.context().localArea());tag.putString("profile",spawn.profile().values().toString());tag.putInt("matching_sellers",spawn.plan().matchingSellers());tag.putInt("generated_offers",spawn.plan().offers().size());mob.getPersistentData().put(KEY,tag);if(mob instanceof EasyNPCBase<?> npc)npc.setObjectiveDataSet(new ObjectiveDataSet());ensure(mob);}
 public static void ensure(PathfinderMob mob){if(!mob.getPersistentData().contains(KEY)||!(mob instanceof EasyNPCBase<?> npc))return;var data=mob.getPersistentData().getCompoundOrEmpty(KEY);int radius=data.getInt("radius").orElse(10);if(radius<1||radius>64)return;var home=new BlockPos(data.getInt("x").orElse(0),data.getInt("y").orElse(0),data.getInt("z").orElse(0));mob.setNoAi(false);mob.setHomeTo(home,radius);var goals=npc.getEntityGoalSelector();
  // Easy NPC may refresh objectives. Remove unrestricted movement/look goals only on managed NPCs.
  goals.removeAllGoals(g->!(g instanceof Managed)&&(g.getFlags().contains(Goal.Flag.MOVE)||g.getFlags().contains(Goal.Flag.LOOK)));
  if(goals.getAvailableGoals().stream().noneMatch(g->g.getGoal() instanceof HomeGoal))goals.addGoal(2,new HomeGoal(mob,home,radius,data.getBoolean("wander").orElse(true)));
  if(data.getBoolean("look").orElse(true)&&goals.getAvailableGoals().stream().noneMatch(g->g.getGoal() instanceof LookGoal)){goals.addGoal(0,new LookGoal(mob));goals.addGoal(1,new AroundGoal(mob));}
 }
 static final class LookGoal extends LookAtPlayerGoal implements Managed{LookGoal(PathfinderMob mob){super(mob,Player.class,8F);}}
 static final class AroundGoal extends RandomLookAroundGoal implements Managed{AroundGoal(PathfinderMob mob){super(mob);}}
 static final class HomeGoal extends Goal implements Managed{
  final PathfinderMob mob;final BlockPos home;final int radius;final boolean wander;Path path;boolean returning;
  HomeGoal(PathfinderMob mob,BlockPos home,int radius,boolean wander){this.mob=mob;this.home=home;this.radius=radius;this.wander=wander;setFlags(EnumSet.of(Flag.MOVE));}
  boolean in(double x,double y,double z){return inside(home.getX()+.5,home.getY(),home.getZ()+.5,x,y,z,radius);}
  @Override public boolean canUse(){if(mob instanceof EasyNPCBase<?> npc&&npc.getTradingPlayer()!=null){mob.getNavigation().stop();return false;}returning=!in(mob.getX(),mob.getY(),mob.getZ());if(returning){path=mob.getNavigation().createPath(home,0);return path!=null;}if(!wander||mob.getRandom().nextInt(80)!=0)return false;
   for(int attempt=0;attempt<16;attempt++){var candidate=home.offset(mob.getRandom().nextInt(radius*2+1)-radius,mob.blockPosition().getY()-home.getY()+mob.getRandom().nextInt(5)-2,mob.getRandom().nextInt(radius*2+1)-radius);if(!in(candidate.getX()+.5,candidate.getY(),candidate.getZ()+.5)||!mob.level().hasChunkAt(candidate))continue;var proposed=mob.getNavigation().createPath(candidate,0);if(proposed==null||!proposed.canReach())continue;boolean bounded=true;for(int i=0;i<proposed.getNodeCount();i++){var node=proposed.getNode(i);if(!in(node.x+.5,node.y,node.z+.5)){bounded=false;break;}}if(bounded){path=proposed;return true;}}
   return false;
  }
  @Override public void start(){mob.getNavigation().moveTo(path,.6);}
  @Override public boolean canContinueToUse(){return !mob.getNavigation().isDone()&&(returning||in(mob.getX(),mob.getY(),mob.getZ()))&&(!(mob instanceof EasyNPCBase<?> npc)||npc.getTradingPlayer()==null);}
  @Override public void stop(){mob.getNavigation().stop();}
 }
 private EconomyMovement(){}
}
