package org.humanjourney.core.advancement;
import java.util.*;
import java.nio.charset.StandardCharsets;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
/** Independent S2C-only protocol1. Bounded raw fragments, never raw authoring data. */
public final class NativePackets {
 public static final int CHUNK=16*1024,MAX_SNAPSHOT=1024*1024,MAX_PARTS=64,MAX_PACKET=24*1024;
 public record Frame(UUID session,long sequence,int part,int parts,int length,byte[] data,boolean invalid){public Frame{data=data.clone();if(!invalid&&(session==null||sequence<1||length<1||length>MAX_SNAPSHOT||parts!=(length+CHUNK-1)/CHUNK||parts>MAX_PARTS||part<0||part>=parts||data.length!=Math.min(CHUNK,length-part*CHUNK)))throw new IllegalArgumentException("invalid native advancement fragment bounds");}@Override public byte[] data(){return data.clone();}public static Frame bad(){return new Frame(null,0,0,0,0,new byte[0],true);}}
 public static List<Frame> frames(UUID session,long sequence,NativePresentation view){byte[] data=view.json().getBytes(StandardCharsets.UTF_8);if(data.length>MAX_SNAPSHOT)throw new IllegalArgumentException("native advancement snapshot exceeds1MiB");int parts=(data.length+CHUNK-1)/CHUNK;var result=new ArrayList<Frame>();for(int i=0;i<parts;i++)result.add(new Frame(session,sequence,i,parts,data.length,Arrays.copyOfRange(data,i*CHUNK,Math.min(data.length,(i+1)*CHUNK)),false));return List.copyOf(result);}
 public static final StreamCodec<RegistryFriendlyByteBuf,Frame> CODEC=new StreamCodec<>(){
  public void encode(RegistryFriendlyByteBuf b,Frame p){if(p.invalid)throw new IllegalArgumentException("bad fragment");int start=b.writerIndex();b.writeUUID(p.session);b.writeVarLong(p.sequence);b.writeVarInt(p.part);b.writeVarInt(p.parts);b.writeVarInt(p.length);b.writeByteArray(p.data);if(b.writerIndex()-start>MAX_PACKET)throw new IllegalArgumentException("oversized native advancement packet");}
  public Frame decode(RegistryFriendlyByteBuf b){try{if(b.readableBytes()>MAX_PACKET)throw new IllegalArgumentException("oversized packet");var p=new Frame(b.readUUID(),b.readVarLong(),b.readVarInt(),b.readVarInt(),b.readVarInt(),b.readByteArray(CHUNK),false);if(b.readableBytes()!=0)throw new IllegalArgumentException("trailing data");return p;}catch(RuntimeException e){b.skipBytes(b.readableBytes());return Frame.bad();}}
 };
 private NativePackets(){}
}
