package org.humanjourney.core.geography;
import java.util.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.ScoreHolder;
import org.humanjourney.core.npc.context.*;
/** Authoritative 2Hz updates with transient per-connection caches; no player-owned discovery progression. */
public final class GeographyServer {
 private static MinecraftServer owner;private static final Map<UUID,GeographySyncTracker> clients=new HashMap<>();private static final Set<UUID> failures=new HashSet<>();
 public static GeographyRoles roles(ServerPlayer player){return GeographyRoles.from(player.getTags());}
 private static EraRules.Scores scores(ServerPlayer p){var board=p.level().getScoreboard();return (objective,holder)->{var o=board.getObjective(objective);if(o==null)return OptionalInt.empty();var value=board.getPlayerScoreInfo(ScoreHolder.forNameOnly(holder),o);return value==null?OptionalInt.empty():OptionalInt.of(value.value());};}
 public static GeographyContext context(ServerPlayer player){return GeographyContext.resolve(NpcContextStore.state().data(),scores(player),player.level().dimension().location().toString(),player.getX(),player.getZ());}
 private static String era(ServerPlayer player){try{return NpcContextStore.state().data().eraRules().resolve(scores(player));}catch(IllegalArgumentException e){return "unknown";}}
 private static boolean send(Object packet,ServerPlayer player){boolean sent=GeographyNetwork.send(packet,player);if(!sent&&failures.add(player.getUUID()))com.mojang.logging.LogUtils.getLogger().warn("[HJ Core] Geography client sync failed for {}; discovery remains authoritative on server",player.getScoreboardName());return sent;}
 private static boolean snapshot(ServerPlayer player,DiscoveryLedger.Snapshot data){for(var part:GeographyPackets.snapshot(data))if(!send(part,player))return false;return true;}
 public static void tick(MinecraftServer server){
  if(owner!=server){clear();owner=server;}if(server.getTickCount()%10!=0)return;var online=server.getPlayerList().getPlayers();var ids=new HashSet<UUID>();for(var player:online)ids.add(player.getUUID());clients.keySet().retainAll(ids);failures.retainAll(ids);
  for(var player:online){var role=roles(player);var cache=clients.computeIfAbsent(player.getUUID(),id->new GeographySyncTracker());var input=new GeographySyncTracker.Input(player.getId(),player.level().dimension().location().toString(),player.getX(),player.getZ(),role.participant()?era(player):"unknown",NpcContextStore.state().revision(),role);boolean joining=cache.joining(input),evaluate=cache.evaluate(input);var view=cache.resolved();
   if(evaluate){try{view=role.participant()?context(player):GeographyContext.EMPTY;}catch(IllegalArgumentException error){view=GeographyContext.EMPTY;if(failures.add(player.getUUID()))com.mojang.logging.LogUtils.getLogger().warn("[HJ Core] Geography resolution failed for {}: {}",player.getScoreboardName(),error.getMessage());}cache.observed(input,view);}
   if(role.participant()){
    var saved=SavedDiscovery.get(server);if(joining||cache.needsSnapshot(saved.snapshot().revision()))if(snapshot(player,saved.snapshot()))cache.synced(saved.snapshot().revision());
    if(evaluate){var delta=saved.discover(role,view);if(delta.changed())publish(server,delta,view,true);}
   }else if(evaluate){cache.synced(-1);}
   var packet=new GeographyPackets.Context(role.participant(),view);if(cache.contextChanged(packet)&&send(packet,player))cache.sent(packet);
  }
 }
 public static void publish(MinecraftServer server,DiscoveryLedger.Delta delta,GeographyContext labels,boolean notify){if(!delta.changed())return;var packet=new GeographyPackets.Discovery(GeographyPackets.Kind.DELTA,delta.revision(),new ArrayList<>(delta.macros()),new ArrayList<>(delta.locals()));for(var player:server.getPlayerList().getPlayers())if(roles(player).participant()){
  var cache=clients.get(player.getUUID());if(cache==null||cache.needsSnapshot(delta.revision()-1)){if(snapshot(player,SavedDiscovery.get(server).snapshot())&&cache!=null)cache.synced(delta.revision());}else if(send(packet,player))cache.synced(delta.revision());
  if(notify){if(!delta.macros().isEmpty())player.sendSystemMessage(Component.translatable("message.human_journey_core.new_region",labels.macroLabel()));if(!delta.locals().isEmpty())player.sendSystemMessage(Component.translatable("message.human_journey_core.new_area",labels.localLabel()));}
 }}
 public static void resync(MinecraftServer server){var data=SavedDiscovery.get(server).snapshot();for(var player:server.getPlayerList().getPlayers())if(roles(player).participant()&&snapshot(player,data)){var cache=clients.get(player.getUUID());if(cache!=null)cache.synced(data.revision());}}
 public static void clear(){owner=null;clients.clear();failures.clear();}
 private GeographyServer(){}
}
