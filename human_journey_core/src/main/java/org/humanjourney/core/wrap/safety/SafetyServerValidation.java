package org.humanjourney.core.wrap.safety;
import java.util.*;
import java.nio.file.*;
import java.nio.*;
import java.util.concurrent.*;
import net.minecraft.server.*;
import net.minecraft.server.level.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.chunk.status.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.vehicle.*;
import net.minecraft.world.item.*;
import net.minecraft.world.phys.*;
import net.minecraftforge.event.server.*;
import net.minecraftforge.event.TickEvent;
import org.humanjourney.core.wrap.*;

/** DEV ONLY. Excluded from runtime JAR. Uses a new CI fixture world, never an installed world. */
public final class SafetyServerValidation {
 private static MinecraftServer server;private static int ticks,phase;private static Throwable failure;
 private static List<CompletableFuture<ChunkResult<ChunkAccess>>> denied;
 private static Set<String> before;private static ExistingDestination loader;
 private static AbstractBoat boat;private static AbstractChestBoat chest;private static ServerPlayer a,b,c;private static UUID boatId,chestId;private static net.minecraft.nbt.CompoundTag savedBoat;
 private static final List<String> checks=new ArrayList<>();
 private static void check(boolean condition,String label){if(!condition)throw new IllegalStateException(label);checks.add(label);}
 public static void register(){
  if(!Path.of("").toAbsolutePath().normalize().endsWith(Path.of("build","safety-server")))throw new IllegalStateException("Safety harness requires isolated build/safety-server working directory");
  com.mojang.logging.LogUtils.getLogger().info("EARTH_SAFETY_HARNESS_REGISTERED phase={}",System.getProperty("humanjourney.safetyPhase"));
  ServerStartedEvent.BUS.addListener(e->{server=e.getServer();});
  TickEvent.ServerTickEvent.Post.BUS.addListener(e->{if(server==null||failure!=null)return;try{run();}catch(Throwable error){failure=error;com.mojang.logging.LogUtils.getLogger().error("EARTH_SAFETY_SERVER_FAILED",error);try{Files.writeString(Path.of("FAILED.txt"),error.toString());}catch(Exception ignored){}server.halt(false);}});
 }
 private static ServerPlayer player(String name,ServerLevel level){
  var p=new ServerPlayer(server,level,new com.mojang.authlib.GameProfile(UUID.randomUUID(),name),ClientInformation.createDefault());
  var connection=new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND);
  p.connection=new net.minecraft.server.network.ServerGamePacketListenerImpl(server,connection,p,net.minecraft.server.network.CommonListenerCookie.createInitial(p.getGameProfile(),false));
  return p;
 }
 private static void acknowledge(ServerPlayer p)throws Exception{var field=net.minecraft.server.network.ServerGamePacketListenerImpl.class.getDeclaredField("awaitingTeleport");field.setAccessible(true);p.connection.handleAcceptTeleportPacket(new net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket(field.getInt(p.connection)));}
 private static void run()throws Exception{
  ticks++;var level=server.overworld();boolean prepare=System.getProperty("humanjourney.safetyPhase","verify").equals("prepare");
  if(prepare){if(phase==0){
   check(!EarthProtection.ready(),"prepare phase protection disabled");com.mojang.logging.LogUtils.getLogger().info("EARTH_SAFETY_PREPARING_FIXTURE");
   for(int center:new int[]{-12240,12272})for(int cx=(center>>4)-3;cx<=(center>>4)+3;cx++)for(int cz=-1;cz<=2;cz++)level.getChunk(cx,cz);
   pool(level,-12288+16,-12288+96);pool(level,12288-32,12288-1);
   phase=1;
  }else if(ticks>20){server.saveAllChunks(false,true,true);Files.writeString(Path.of("PREPARED.txt"),"new isolated fixture prepared; production terrain untouched");server.halt(false);server=null;}return;}
  if(phase==0){
   check(EarthProtection.ready(),"protection active before world load after real server restart");
   before=records();denied=new ArrayList<>();
   // Equivalent native FULL requests used by view-distance/player-loading tickets.
   for(int x=768;x<=770;x++)for(int z=100;z<=102;z++)denied.add(level.getChunkSource().getChunkFuture(x,z,ChunkStatus.FULL,true));
   denied.add(level.getChunkSource().getChunkFuture(600,100,ChunkStatus.FULL,true));
   loader=new ExistingDestination(level,List.of(new AABB(-12256.7,63.5,7.3,-12255.3,64.2,8.7)));
   phase=1;return;
  }
  if(ticks>600)throw new IllegalStateException("native validation timeout phase="+phase+" "+SafetyRuntime.status());
  if(phase==1){
   if(!denied.stream().allMatch(CompletableFuture::isDone))return;
   for(var future:denied)check(!future.join().isSuccess(),"missing/outside FULL request finishes without generation");
   if(!loader.ready()){if(loader.failed())throw new IllegalStateException(loader.failure);return;}
   check(ExistingDestination.ticketCount()>0,"existing destination loaded with owned tickets after restart");loader.close();check(ExistingDestination.ticketCount()==0,"destination tickets released");
   var failedLoader=new ExistingDestination(level,List.of(new AABB(12288,64,1600,12289,66,1601)));check(failedLoader.failed(),"outside dependency rejected before ticket creation");failedLoader.close();check(ExistingDestination.ticketCount()==0,"failed loader leaves no tickets");
   a=player("EarthTestA",level);b=player("EarthTestB",level);
   boat=(AbstractBoat)EntityType.OAK_BOAT.create(level,EntitySpawnReason.COMMAND);check(boat!=null,"native chest boat created");boat.absSnapTo(12286,63.5625,8,90,0);level.addFreshEntity(boat);boatId=boat.getUUID();
   chest=(AbstractChestBoat)EntityType.OAK_CHEST_BOAT.create(level,EntitySpawnReason.COMMAND);chest.absSnapTo(12286,63.5625,12,90,0);chest.setItem(0,new ItemStack(Items.DIAMOND,7));chestId=chest.getUUID();level.addFreshEntity(chest);c=player("EarthTestC",level);c.absSnapTo(12286,64.1,12,37,-10);level.addNewPlayer(c);server.getPlayerList().getPlayers().add(c);check(c.startRiding(chest,true),"native chest-boat rider mounted");
   for(var p:List.of(a,b)){p.absSnapTo(12286,64.1,8,37,-10);level.addNewPlayer(p);server.getPlayerList().getPlayers().add(p);check(p.startRiding(boat,true),"native player mounts boat");}
   phase=2;return;
  }
  if(phase==2){
   check(boat.getPassengers().size()==2,"two actual passengers retained");
   check(SafetyRuntime.packet(a,12288.25,8),"outward vehicle proposal held before position acceptance");
   check(boat.getX()<12288&&a.getRootVehicle()==boat&&b.getRootVehicle()==boat,"held group stays intact inside canonical boundary");check(SafetyRuntime.packet(c,12288.25,12),"independent chest-boat crossing held/prepared");acknowledge(a);acknowledge(b);acknowledge(c);phase=3;return;
  }
  if(phase==3){
   acknowledge(a);acknowledge(b);acknowledge(c);
   if(boat.getX()>0||chest.getX()>0)return;
   check(boat.getUUID().equals(boatId)&&chest.getUUID().equals(chestId)&&chest.getItem(0).is(Items.DIAMOND)&&chest.getItem(0).getCount()==7,"crossing preserves real vehicle UUID and inventory");
   check(a.getRootVehicle()==boat&&b.getRootVehicle()==boat&&boat.getPassengers().size()==2,"crossing preserves both real passenger links");
   check(SafetyRuntime.status().contains("CROSSED")&&SafetyRuntime.status().contains("crossed=true"),"successful crossing explicitly diagnosed");
   check(ExistingDestination.ticketCount()==0,"successful crossing releases tickets");
   // Native save/load vehicle identity and inventory; no new runtime UUID rewrite.
   var output=net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess());chest.saveWithoutId(output);var tag=output.buildResult();savedBoat=tag;var copy=(AbstractChestBoat)EntityType.OAK_CHEST_BOAT.create(level,EntitySpawnReason.COMMAND);copy.load(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING,level.registryAccess(),tag));check(copy.getUUID().equals(chestId)&&copy.getItem(0).getCount()==7,"native chest-boat save/load retains identity and inventory");
   phase=4;return;
  }
  if(phase==4){
   SafetyRuntime.closeAll();check(ExistingDestination.ticketCount()==0,"stop/reconnect cleanup releases all owned tickets");
   server.saveAllChunks(false,true,true);var after=records();check(after.equals(before),"native FULL/view-distance/dependency requests create zero new region chunk records under protection");
   Files.writeString(Path.of("EARTH_SAFETY_SERVER_OK.json"),new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(Map.of("checks",checks,"count",checks.size(),"tick",ticks,"new_chunk_records",0,"live_gameplay_acceptance",false)));
   com.mojang.logging.LogUtils.getLogger().info("EARTH_SAFETY_SERVER_OK: {} checks, real dedicated restart, native chunk scheduler/disk/tickets, real chest boat and two simulated connections; no real client/gameplay acceptance",checks.size());
   server.halt(false);server=null;
  }
 }
 private static void pool(ServerLevel level,int west,int east){for(int x=west;x<=east;x++)for(int z=0;z<=16;z++){level.setBlock(new BlockPos(x,62,z),Blocks.STONE.defaultBlockState(),2);level.setBlock(new BlockPos(x,63,z),Blocks.WATER.defaultBlockState(),2);level.setBlock(new BlockPos(x,64,z),Blocks.AIR.defaultBlockState(),2);level.setBlock(new BlockPos(x,65,z),Blocks.AIR.defaultBlockState(),2);}}
 private static Set<String> records()throws Exception{var result=new TreeSet<String>();try(var files=Files.list(server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).resolve("region"))){for(var p:files.filter(p->p.toString().endsWith(".mca")).toList()){try(var in=Files.newInputStream(p)){byte[] header=in.readNBytes(4096);for(int i=0;i<1024;i++)if(ByteBuffer.wrap(header,i*4,4).getInt()!=0)result.add(p.getFileName()+":"+i);}}}return result;}
 private SafetyServerValidation(){}
}
