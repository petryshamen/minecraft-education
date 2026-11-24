package org.humanjourney.core.progress;
import java.util.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.ScoreHolder;
import org.humanjourney.core.geography.GeographyRoles;
/** Read-only shared scoreboard sampling at1Hz; sends only on content/role/connection change. */
public final class ProgressServer {
 private static MinecraftServer owner;private static UUID session=UUID.randomUUID();private static long sequence,revision;private static ProgressView last;private static final Map<UUID,ProgressDelivery> clients=new HashMap<>();private static Set<String> unresolved=Set.of();
 public static Set<String> unresolved(){return unresolved;}
 public static void clear(){owner=null;session=UUID.randomUUID();sequence=0;revision=0;last=null;clients.clear();unresolved=Set.of();}
 public static void tick(MinecraftServer server){if(owner!=server){clear();owner=server;}if(server.getTickCount()%20!=0)return;var graph=ProgressStore.state().graph();var board=server.getScoreboard();var values=new HashMap<String,OptionalInt>();var missing=new TreeSet<String>();for(var ref:graph.references())if(ref.type().equals("score")){String key=ref.objective()+"\u0000"+ref.holder();values.computeIfAbsent(key,k->{var objective=board.getObjective(ref.objective());var score=objective==null?null:board.getPlayerScoreInfo(ScoreHolder.forNameOnly(ref.holder()),objective);if(score==null){missing.add(ref.objective()+" / "+ref.holder());return OptionalInt.empty();}return OptionalInt.of(score.value());});}var errors=Set.copyOf(missing);if(!errors.equals(unresolved)){unresolved=errors;if(!errors.isEmpty())com.mojang.logging.LogUtils.getLogger().warn("[HJ Core] Civilization graph unresolved shared scores (locked, not created): {}",errors);}var next=ProgressView.project(graph,(o,h)->values.getOrDefault(o+"\u0000"+h,OptionalInt.empty()));if(!next.equals(last)){last=next;revision++;}var online=server.getPlayerList().getPlayers();var ids=new HashSet<UUID>();for(var player:online){ids.add(player.getUUID());boolean participant=GeographyRoles.from(player.getTags()).participant();var tracker=clients.computeIfAbsent(player.getUUID(),id->new ProgressDelivery());if(!tracker.needs(player.getId(),participant,revision))continue;boolean sent=true;var view=participant?next:ProgressView.empty(false);for(var frame:ProgressPackets.frames(session,++sequence,view))if(!ProgressNetwork.send(frame,player)){sent=false;break;}if(sent)tracker.sent(player.getId(),participant,revision);}clients.keySet().retainAll(ids);}
 private ProgressServer(){}
}
