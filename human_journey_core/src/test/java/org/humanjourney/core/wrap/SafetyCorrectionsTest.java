package org.humanjourney.core.wrap;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;
import java.util.*;
import java.nio.file.*;
import net.minecraft.server.level.*;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.phys.*;
import org.humanjourney.core.wrap.safety.*;
class SafetyCorrectionsTest {
 static MockedStatic<net.minecraftforge.fml.ModLoader> loader;
 @BeforeAll static void init(){loader=mockStatic(net.minecraftforge.fml.ModLoader.class);loader.when(net.minecraftforge.fml.ModLoader::get).thenReturn(mock(net.minecraftforge.fml.ModLoader.class));net.minecraft.SharedConstants.tryDetectVersion();net.minecraft.server.Bootstrap.bootStrap();}
 @AfterAll static void end(){loader.close();}
 @Test void bothOptionsDefaultOff(){assertFalse(WrapConfig.ENABLED.getDefault());assertFalse(WrapConfig.PROTECTION.getDefault());assertEquals(0,WrapConfig.ARRIVAL_INSET.getDefault());}
 @Test void exactChunkInterval(){assertTrue(SafetyPolicy.chunkInside(-768,-384));assertTrue(SafetyPolicy.chunkInside(767,383));assertFalse(SafetyPolicy.chunkInside(768,0));assertFalse(SafetyPolicy.chunkInside(-769,0));}
 @Test void noNorthSouthWrap(){assertFalse(SafetyPolicy.chunkInside(0,-385));assertFalse(SafetyPolicy.chunkInside(0,384));}
 @Test void fullFootprintInside(){assertTrue(SafetyPolicy.inside(-12287,12287,-6143,6143));assertFalse(SafetyPolicy.inside(-12288.1,-12287,0,1));assertFalse(SafetyPolicy.inside(12287,12288,0,1));}
 @Test void dependencyBandIsExplicitAndInsideCanonicalEarth(){assertEquals(16,SafetyPolicy.BUFFER);assertFalse(SafetyPolicy.readable(12280,0));assertFalse(SafetyPolicy.readable(-12280,0));assertTrue(SafetyPolicy.readable(12270,0));assertTrue(SafetyPolicy.readable(-12270,0));}
 @Test void rawPositionCannotBypassContainment()throws Exception{assertTrue(source("mixin/EarthMovementMixin").contains("method=\"setPosRaw\""));assertTrue(source("safety/SafetyRuntime").contains("!entity.isAddedToWorld()"));}
 @Test void nonFiniteBoundsRejected(){assertFalse(SafetyPolicy.inside(Double.NaN,0,0,0));assertFalse(SafetyPolicy.inside(0,Double.POSITIVE_INFINITY,0,0));}
 @Test void clampInside(){assertEquals(12286,SafetyPolicy.clamp(12290,-12286,12286));assertEquals(-12286,SafetyPolicy.clamp(-12300,-12286,12286));assertEquals(1.25,SafetyPolicy.clamp(1.25,-12286,12286));}
 @Test void invalidContainmentRejected(){assertThrows(IllegalArgumentException.class,()->SafetyPolicy.clamp(Double.NaN,0,1));assertThrows(IllegalArgumentException.class,()->SafetyPolicy.clamp(0,2,1));}
 net.minecraft.nbt.CompoundTag tag(){var t=new net.minecraft.nbt.CompoundTag();t.putString("Status","minecraft:full");t.putInt("xPos",3);t.putInt("zPos",4);t.put("sections",new net.minecraft.nbt.ListTag());return t;}
 @Test void fullMetadataAccepted(){assertTrue(EarthProtection.full(tag(),new net.minecraft.world.level.ChunkPos(3,4)));}
 @Test void incompleteRejected(){var t=tag();t.putString("Status","minecraft:noise");assertFalse(EarthProtection.full(t,new net.minecraft.world.level.ChunkPos(3,4)));}
 @Test void wrongCoordinatesRejected(){assertFalse(EarthProtection.full(tag(),new net.minecraft.world.level.ChunkPos(4,3)));}
 @Test void missingSectionsRejected(){var t=tag();t.remove("sections");assertFalse(EarthProtection.full(t,new net.minecraft.world.level.ChunkPos(3,4)));}
 @Test void missingStatusRejected(){var t=tag();t.remove("Status");assertFalse(EarthProtection.full(t,new net.minecraft.world.level.ChunkPos(3,4)));}
 @Test void groupIdentityAndMountGraphRetained(){var root=mock(AbstractBoat.class);var a=mock(ServerPlayer.class);var b=mock(ServerPlayer.class);when(root.getPassengers()).thenReturn(List.of(a,b));when(a.getPassengers()).thenReturn(List.of());when(b.getPassengers()).thenReturn(List.of());assertEquals(Set.of(root,a,b),new HashSet<>(SafetyRuntime.group(root)));verify(root,never()).ejectPassengers();verify(a,never()).removeVehicle();}
 @Test void cycleRejected(){var root=mock(AbstractBoat.class);when(root.getPassengers()).thenReturn(List.of(root));assertThrows(IllegalArgumentException.class,()->SafetyRuntime.group(root));}
 @Test void oversizedGroupRejected(){var root=mock(AbstractBoat.class);var children=new ArrayList<Entity>();for(int i=0;i<17;i++)children.add(mock(Entity.class));when(root.getPassengers()).thenReturn(children);assertThrows(IllegalArgumentException.class,()->SafetyRuntime.group(root));}
 static final Path ROOT=Path.of(System.getProperty("humanjourney.projectDir","."));
 String source(String path)throws Exception{return Files.readString(ROOT.resolve("src/main/java/org/humanjourney/core/wrap/"+path+".java"));}
 @Test void packetContainmentRunsAfterMainThreadDispatch()throws Exception{var s=source("mixin/EarthPacketMixin");assertTrue(s.contains("handleMovePlayer"));assertTrue(s.contains("handleMoveVehicle"));assertTrue(s.contains("shift=At.Shift.AFTER"));assertTrue(s.contains("ensureRunningOnSameThread"));}
 @Test void serverMovementAlsoContained()throws Exception{assertTrue(source("mixin/EarthMovementMixin").contains("@ModifyVariable(method=\"move\""));}
 @Test void vehiclePacketsRequireTheControllingPassenger()throws Exception{assertTrue(source("mixin/EarthPacketMixin").contains("root.getControllingPassenger()==player"));}
 @Test void noGenerationAndNoPlaceholderSave()throws Exception{var s=source("mixin/EarthHolderMixin");assertTrue(s.contains("persisted.isBefore(step.targetStatus())"));assertTrue(s.contains("EarthProtection.reject(this)"));assertTrue(source("mixin/EarthMapMixin").contains("chunk.getPersistedStatus()!=ChunkStatus.FULL"));}
 @Test void cancelledDependencyCompletesCentreFuture()throws Exception{assertTrue(source("mixin/EarthTaskMixin").contains("\"waitForScheduledLayer\",\"scheduleChunkInLayer\""));assertTrue(source("mixin/EarthTaskMixin").contains("getCenter()).hjEarth$fail()"));}
 @Test void deadlinesOwnershipAndCleanupExist()throws Exception{var s=source("safety/ExistingDestination");assertTrue(s.contains("removeTicketWithRadius(TYPE.get()"));assertTrue(s.contains("TICKETS.getOrDefault"));assertTrue(s.contains("241,false"));assertFalse(s.contains("getChunkFuture("));assertTrue(source("safety/SafetyRuntime").contains("level.getGameTime()+200"));}
 @Test void failureCannotFallThroughToOldUnsafeLoop()throws Exception{var s=source("WrapBootstrap");assertFalse(s.contains("e->WrapRuntime.tick"));assertTrue(s.contains("SafetyRuntime.tick"));assertTrue(s.contains("ServerAboutToStartEvent"));assertTrue(s.contains("ServerStoppingEvent"));}
 @Test void resetDropsPendingStateAndRestartsDefaultScope()throws Exception{EarthProtection.clear();assertFalse(EarthProtection.ready());assertTrue(source("safety/EarthProtection").contains("epoch!=session"));assertTrue(source("safety/SafetyRuntime").contains("JOBS.clear()"));}
 @Test void diagnosticsDisambiguateActualCrossing()throws Exception{var s=source("safety/SafetyRuntime");for(var label:List.of("LOADING","HELD","CROSSED","FAILED","crossed=","actual=","canonicalX=","inward_adjustment="))assertTrue(s.contains(label),label);}
 @Test void noLiveWorldOrXaeroDependenciesInHarness()throws Exception{var s=source("safety/SafetyServerValidation");assertFalse(s.contains("AppData"));assertFalse(s.contains("\".minecraft\""));assertFalse(s.contains("xaero"));assertTrue(Files.readString(ROOT.resolve("build.gradle")).contains("exclude 'org/humanjourney/core/wrap/safety/SafetyServerValidation*'"));}
}
