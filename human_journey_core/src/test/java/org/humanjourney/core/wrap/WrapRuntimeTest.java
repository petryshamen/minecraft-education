package org.humanjourney.core.wrap;
import java.util.*;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.level.*;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.*;

/** Mocked world preflight and native primitive wiring, not boat/gameplay tests. */
class WrapRuntimeTest {
 static MockedStatic<net.minecraftforge.fml.ModLoader> loader;
 @BeforeAll static void init(){loader=mockStatic(net.minecraftforge.fml.ModLoader.class);loader.when(net.minecraftforge.fml.ModLoader::get).thenReturn(mock(net.minecraftforge.fml.ModLoader.class));SharedConstants.tryDetectVersion();Bootstrap.bootStrap();}
 @AfterAll static void close(){loader.close();}
 net.minecraft.world.level.material.FluidState water;
 ServerLevel level;ServerChunkCache chunks;Entity root;
 @BeforeEach void setup(){
  water=mock(net.minecraft.world.level.material.FluidState.class);when(water.isEmpty()).thenReturn(false);when(water.is(net.minecraft.tags.FluidTags.WATER)).thenReturn(true);
  level=mock(ServerLevel.class);chunks=mock(ServerChunkCache.class);when(level.getChunkSource()).thenReturn(chunks);when(chunks.getChunkNow(anyInt(),anyInt())).thenReturn(mock(LevelChunk.class));
  var border=mock(net.minecraft.world.level.border.WorldBorder.class);when(level.getWorldBorder()).thenReturn(border);when(border.isWithinBounds(any(AABB.class))).thenReturn(true);
  when(level.getMinY()).thenReturn(-64);when(level.getMaxY()).thenReturn(320);when(level.noCollision(any(Entity.class),any(AABB.class))).thenReturn(true);
  when(level.getBlockState(any(BlockPos.class))).thenReturn(Blocks.AIR.defaultBlockState());when(level.getFluidState(any(BlockPos.class))).thenReturn(water);
  when(level.getFluidState(any(BlockPos.class))).thenAnswer(i->((BlockPos)i.getArgument(0)).getY()<63?water:Fluids.EMPTY.defaultFluidState());
  root=boat();
 }
 <T extends Entity>T body(Class<T> type){var e=mock(type);when(e.isAlive()).thenReturn(true);when(e.level()).thenReturn(level);when(e.position()).thenReturn(new Vec3(12288.5,63,0.5));when(e.getDeltaMovement()).thenReturn(new Vec3(.5,0,.25));when(e.getKnownMovement()).thenReturn(new Vec3(.5,0,.25));when(e.getBoundingBox()).thenReturn(new AABB(12288,63,0,12289,64,.9));when(e.getPassengers()).thenReturn(List.of());return e;}
 AbstractBoat boat(){return body(AbstractBoat.class);}
 ServerPlayer player(){var e=body(ServerPlayer.class);e.connection=mock(ServerGamePacketListenerImpl.class,withSettings().extraInterfaces(WrapConnection.class));when(e.getAbilities()).thenReturn(new Abilities());return e;}
 String problem(Entity e){return new WrapRuntime.Member(e,e,level).problem(-24576);}
 @Test void boatNeedsWaterAndAcceptsLoadedWater(){assertNull(problem(root));}
 @Test void boatRejectsDryGround(){when(level.getFluidState(any())).thenReturn(Fluids.EMPTY.defaultFluidState());assertTrue(problem(root).contains("boat destination"));}
 @Test void rejectsLava(){when(level.getFluidState(any())).thenReturn(Fluids.LAVA.defaultFluidState());assertTrue(problem(root).contains("fluid"));}
 @Test void rejectsUnloadedChunks(){when(chunks.getChunkNow(anyInt(),anyInt())).thenReturn(null);assertTrue(problem(root).contains("not loaded"));verify(chunks,never()).getChunk(anyInt(),anyInt(),any(),anyBoolean());}
 @Test void rejectsObstruction(){when(level.noCollision(any(Entity.class),any(AABB.class))).thenReturn(false);assertTrue(problem(root).contains("collision"));}
 @Test void respectsWorldBorder(){when(level.getWorldBorder().isWithinBounds(any(AABB.class))).thenReturn(false);assertTrue(problem(root).contains("world border"));}
 @Test void refusesHazards(){when(level.getBlockState(any())).thenReturn(Blocks.MAGMA_BLOCK.defaultBlockState());assertTrue(problem(root).contains("hazardous"));}
 @Test void refusesDeadMember(){when(root.isAlive()).thenReturn(false);assertTrue(problem(root).contains("dead"));}
 @Test void refusesChangedMountTopology(){var m=new WrapRuntime.Member(root,root,level);var child=boat();when(root.getPassengers()).thenReturn(List.of(child));assertTrue(m.problem(-24576).contains("changed"));}
 @Test void submergedBoatRejected(){when(level.getFluidState(any())).thenReturn(water);assertTrue(problem(root).contains("submerged"));}
 @Test void horseNeedsLand(){var horse=body(AbstractHorse.class);assertTrue(problem(horse).contains("horse destination"));}
 @Test void horseOnSupportedLand(){var horse=body(AbstractHorse.class);when(level.getFluidState(any())).thenReturn(Fluids.EMPTY.defaultFluidState());when(level.getBlockCollisions(eq(horse),any())).thenReturn(List.of(net.minecraft.world.phys.shapes.Shapes.block()));assertNull(problem(horse));}
 @Test void unsupportedAirPlayerRejected(){var p=player();when(level.getFluidState(any())).thenReturn(Fluids.EMPTY.defaultFluidState());when(level.getBlockCollisions(eq(p),any())).thenReturn(List.of());assertTrue(problem(p).contains("support"));}
 @Test void creativeFlightRetainsAltitude(){var p=player();p.getAbilities().flying=true;when(level.getFluidState(any())).thenReturn(Fluids.EMPTY.defaultFluidState());assertNull(problem(p));}
 @Test void pendingTeleportDefersWholePlayer(){var p=player();when(((WrapConnection)p.connection).hjWrap$pending()).thenReturn(true);assertTrue(problem(p).contains("acknowledgement"));}
 @Test void shiftedSnapshotPreservesNativeVelocityAndAngles(){when(root.getYRot()).thenReturn(137.5f);when(root.getXRot()).thenReturn(-12.5f);var m=new WrapRuntime.Member(root,root,level);var after=m.shifted(-24576);assertEquals(new Vec3(-12287.5,63,.5),after.position());assertEquals(root.getDeltaMovement(),after.deltaMovement());assertEquals(137.5f,after.yRot());assertEquals(-12.5f,after.xRot());}
 @Test void snapshotUsesNativeKnownMovementRatherThanAssumedDelta(){when(root.getKnownMovement()).thenReturn(new Vec3(.8,0,.4));assertEquals(new Vec3(.8,0,.4),new WrapRuntime.Member(root,root,level).shifted(-24576).deltaMovement());}
 @Test void moveUsesExistingEntityNoRecreation(){var m=new WrapRuntime.Member(root,root,level);m.move(-24576);verify(root).teleportSetPosition(m.shifted(-24576),Set.of());verify(root,never()).remove(any());verify(root,never()).ejectPassengers();}
 @Test void playerPublishUsesAcknowledgedNativeConnection(){var p=player();var m=new WrapRuntime.Member(p,root,level);m.publish();verify(p.connection).teleport(any(PositionMoveRotation.class),eq(Set.of()));verify(p.connection).resetPosition();verify((WrapConnection)p.connection).hjWrap$resetVehicle(root);verify(p,never()).removeVehicle();}
 @Test void publishingRetainsPreflightMovementAfterNativeMovementReset(){var p=player();var m=new WrapRuntime.Member(p,root,level);m.move(-24576);when(p.getKnownMovement()).thenReturn(Vec3.ZERO);m.publish();verify(p.connection).teleport(m.shifted(-24576),Set.of());assertEquals(new Vec3(.5,0,.25),m.shifted(-24576).deltaMovement());}
}
