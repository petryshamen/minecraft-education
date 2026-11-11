package org.humanjourney.core.geography;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.humanjourney.core.npc.context.NpcContextStore;
class GeographyServerTest {
 private static MockedStatic<net.minecraftforge.fml.ModLoader> loader;
 @BeforeAll static void nativeRegistries(){loader=mockStatic(net.minecraftforge.fml.ModLoader.class);loader.when(net.minecraftforge.fml.ModLoader::get).thenReturn(mock(net.minecraftforge.fml.ModLoader.class));SharedConstants.tryDetectVersion();Bootstrap.bootStrap();}
 @AfterAll static void closeLoader(){if(loader!=null)loader.close();}
 MinecraftServer server;SavedDiscovery saved;List<ServerPlayer> online;Map<UUID,GeographyContext> views;Map<UUID,Set<String>> tags;Map<UUID,Double> positions;Map<UUID,List<Object>> packets;AtomicInteger clock;
 MockedStatic<SavedDiscovery> saves;MockedStatic<GeographyNetwork> network;MockedStatic<GeographyServer> runtime;
 @BeforeEach void setup(){GeographyServer.clear();NpcContextStore.reset();server=mock(MinecraftServer.class);var players=mock(PlayerList.class);online=new ArrayList<>();views=new HashMap<>();tags=new HashMap<>();positions=new HashMap<>();packets=new HashMap<>();clock=new AtomicInteger(10);saved=new SavedDiscovery();when(server.getPlayerList()).thenReturn(players);when(players.getPlayers()).thenAnswer(i->online);when(server.getTickCount()).thenAnswer(i->clock.get());
  saves=mockStatic(SavedDiscovery.class,invocation->invocation.getMethod().getName().equals("get")?saved:invocation.callRealMethod());
  network=mockStatic(GeographyNetwork.class);network.when(()->GeographyNetwork.send(any(),any(ServerPlayer.class))).thenAnswer(i->{ServerPlayer player=i.getArgument(1);packets.get(player.getUUID()).add(i.getArgument(0));return true;});
  runtime=mockStatic(GeographyServer.class,invocation->{String name=invocation.getMethod().getName();if(name.equals("context")){ServerPlayer player=invocation.getArgument(0);return views.get(player.getUUID());}if(name.equals("era"))return "era_test";return invocation.callRealMethod();});
 }
 @AfterEach void close(){runtime.close();network.close();saves.close();GeographyServer.clear();NpcContextStore.reset();}
 ServerPlayer player(String name,String... roleTags){var p=mock(ServerPlayer.class);var level=mock(ServerLevel.class);var id=UUID.nameUUIDFromBytes(name.getBytes(java.nio.charset.StandardCharsets.UTF_8));tags.put(id,new HashSet<>(List.of(roleTags)));positions.put(id,0D);packets.put(id,new ArrayList<>());views.put(id,new GeographyContext("macro_a","Macro A","local_a","Local A"));when(p.getUUID()).thenReturn(id);when(p.getId()).thenReturn(online.size()+1);when(p.getScoreboardName()).thenReturn(name);when(p.getTags()).thenAnswer(i->tags.get(id));when(p.level()).thenReturn(level);when(level.dimension()).thenReturn(Level.OVERWORLD);when(p.getX()).thenAnswer(i->positions.get(id));when(p.getZ()).thenReturn(0D);online.add(p);return p;}
 void tick(){GeographyServer.tick(server);clock.addAndGet(10);}
 long discoveryPackets(ServerPlayer p){return packets.get(p.getUUID()).stream().filter(o->o instanceof GeographyPackets.Discovery).count();}
 @Test void joinDiscoveryBroadcastAndRepeatedMovementHaveChangeOnlyTraffic(){var teacher=player("teacher","hj_participant","hj_teacher");var one=player("one","hj_participant","hj_student");var two=player("two","hj_participant","hj_student");var outsider=player("outsider");tick();assertEquals(Set.of("macro_a"),saved.snapshot().macros());assertEquals(Set.of("local_a"),saved.snapshot().locals());assertTrue(discoveryPackets(teacher)>0);assertTrue(discoveryPackets(two)>0);assertEquals(0,discoveryPackets(outsider));verify(teacher,times(2)).sendSystemMessage(any(Component.class));verify(two,times(2)).sendSystemMessage(any(Component.class));var counts=new HashMap<UUID,Integer>();packets.forEach((id,list)->counts.put(id,list.size()));for(int i=0;i<5;i++){positions.put(one.getUUID(),(double)i+1);tick();}packets.forEach((id,list)->assertEquals(counts.get(id),list.size()));verify(teacher,times(2)).sendSystemMessage(any(Component.class));}
 @Test void conflictingTeacherRoleUpdatesHudWithoutDiscovery(){var p=player("teacher","hj_participant","hj_teacher","hj_student");tick();views.put(p.getUUID(),new GeographyContext("new_macro","New Macro","new_local","New Local"));positions.put(p.getUUID(),50D);tick();assertTrue(saved.snapshot().macros().isEmpty());assertTrue(saved.snapshot().locals().isEmpty());assertEquals(2,packets.get(p.getUUID()).stream().filter(o->o instanceof GeographyPackets.Context).count());verify(p,never()).sendSystemMessage(any(Component.class));}
 @Test void roleChangeAndAdminResetSynchroniseWithoutStationaryRediscovery(){var p=player("student","hj_participant","hj_student");tick();assertFalse(saved.snapshot().macros().isEmpty());saved.reset();GeographyServer.resync(server);int count=packets.get(p.getUUID()).size();tick();assertTrue(saved.snapshot().macros().isEmpty());assertEquals(count,packets.get(p.getUUID()).size());positions.put(p.getUUID(),1D);tick();assertTrue(saved.snapshot().macros().contains("macro_a"));tags.get(p.getUUID()).clear();tick();assertTrue(packets.get(p.getUUID()).getLast() instanceof GeographyPackets.Context c&&!c.participant());}
 @Test void macroOnlyStudentDiscoveryDoesNotWaitForLocalCoverage(){var p=player("student","hj_participant","hj_student");views.put(p.getUUID(),new GeographyContext("macro_only","Macro Only","",""));tick();assertEquals(Set.of("macro_only"),saved.snapshot().macros());assertTrue(saved.snapshot().locals().isEmpty());verify(p,times(1)).sendSystemMessage(any(Component.class));}
}
