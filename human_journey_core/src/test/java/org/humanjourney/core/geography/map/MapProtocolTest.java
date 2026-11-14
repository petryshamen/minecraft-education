package org.humanjourney.core.geography.map;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockedStatic;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.humanjourney.core.water.WaterGeography;

class MapProtocolTest {
 static MockedStatic<net.minecraftforge.fml.ModLoader> loader;
 @BeforeAll static void nativeRegistries(){loader=mockStatic(net.minecraftforge.fml.ModLoader.class);loader.when(net.minecraftforge.fml.ModLoader::get).thenReturn(mock(net.minecraftforge.fml.ModLoader.class));SharedConstants.tryDetectVersion();Bootstrap.bootStrap();}
 @AfterAll static void closeLoader(){loader.close();}
 static RegistryFriendlyByteBuf buffer(){return new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));}
 @Test void nativeCodecRoundTripContainsMinimalFieldsOnly(){var a=MapGeometryTest.area(MapArea.Kind.LOCAL,"visible",3,"",0,20);var frame=MapGeometryTest.full(1,List.of(a)).getFirst();var b=buffer();try{MapPackets.CODEC.encode(b,frame);assertEquals(frame,MapPackets.CODEC.decode(b));}finally{b.release();}}
 @Test void maximumVertexPacketFitsBoundedTransfer(){var points=new ArrayList<WaterGeography.Point>();for(int i=0;i<256;i++){double t=2*Math.PI*i/256;points.add(new WaterGeography.Point(Math.cos(t)*1000,Math.sin(t)*1000));}var areas=new ArrayList<MapArea>();for(int i=0;i<8;i++)areas.add(MapArea.decoded(MapArea.Kind.LOCAL,"a_"+i,"Label "+i,MapGeometryTest.DIM,i,"",points));var b=buffer();try{var f=MapGeometryTest.full(1,areas).getFirst();MapPackets.CODEC.encode(b,f);assertTrue(b.readableBytes()<=MapPackets.MAX_BYTES);assertEquals(f,MapPackets.CODEC.decode(b));}finally{b.release();}}
 @Test void oversizedPacketAndTruncatedPacketAreRejectedWithoutThrowing(){var b=buffer();try{b.writeZero(MapPackets.MAX_BYTES+1);assertFalse(MapPackets.CODEC.decode(b).valid());assertEquals(0,b.readableBytes());b.clear();b.writeByte(1);assertFalse(MapPackets.CODEC.decode(b).valid());}finally{b.release();}}
 @Test void invalidAreaCountAndTrailingBytesAreRejected(){var b=buffer();try{var h=MapGeometryTest.header(1);b.writeUUID(h.session());b.writeVarLong(1);b.writeVarLong(1);b.writeVarLong(1);b.writeVarLong(1);b.writeVarInt(0);b.writeVarLong(0);b.writeVarInt(0);b.writeVarInt(1);b.writeVarInt(9);assertFalse(MapPackets.CODEC.decode(b).valid());b.clear();MapPackets.CODEC.encode(b,MapGeometryTest.full(1,List.of()).getFirst());b.writeByte(7);assertFalse(MapPackets.CODEC.decode(b).valid());}finally{b.release();}}
 @Test void senderRejectsUnboundedPartsAndInvalidRevision(){assertThrows(IllegalArgumentException.class,()->new MapPackets.Header(UUID.randomUUID(),0,0,0,0));assertThrows(IllegalArgumentException.class,()->new MapPackets.Frame(MapGeometryTest.header(1),MapPackets.Mode.FULL,0,0,1025,List.of(),List.of(),null));assertThrows(IllegalArgumentException.class,()->new MapPackets.Frame(MapGeometryTest.header(1),MapPackets.Mode.DELTA,1,0,1,List.of(),List.of(),null));}
}
