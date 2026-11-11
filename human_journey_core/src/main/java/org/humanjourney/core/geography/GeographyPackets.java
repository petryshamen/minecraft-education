package org.humanjourney.core.geography;
import java.util.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
public final class GeographyPackets {
 public record Context(boolean participant,GeographyContext geography){}
 public enum Kind{SINGLE,START,PART,END,DELTA}
 public record Discovery(Kind kind,long revision,List<String> macros,List<String> locals){public Discovery{macros=List.copyOf(macros);locals=List.copyOf(locals);if(revision<0||macros.size()>128||locals.size()>128)throw new IllegalArgumentException("invalid discovery packet bounds");for(String id:macros)if(!DiscoveryLedger.validId(id))throw new IllegalArgumentException("invalid macro ID");for(String id:locals)if(!DiscoveryLedger.validId(id))throw new IllegalArgumentException("invalid local ID");}}
 public static final StreamCodec<RegistryFriendlyByteBuf,Context> CONTEXT=new StreamCodec<>(){
  public void encode(RegistryFriendlyByteBuf b,Context p){b.writeBoolean(p.participant());var g=p.geography();b.writeUtf(g.macroId(),64);b.writeUtf(g.macroLabel(),128);b.writeUtf(g.localId(),64);b.writeUtf(g.localLabel(),128);}
  public Context decode(RegistryFriendlyByteBuf b){return new Context(b.readBoolean(),new GeographyContext(b.readUtf(64),b.readUtf(128),b.readUtf(64),b.readUtf(128)));}
 };
 private static void ids(RegistryFriendlyByteBuf b,List<String> ids){b.writeVarInt(ids.size());for(String id:ids)b.writeUtf(id,64);}
 private static List<String> ids(RegistryFriendlyByteBuf b){int size=b.readVarInt();if(size<0||size>128)throw new IllegalArgumentException("invalid discovery list size");var ids=new ArrayList<String>();for(int i=0;i<size;i++)ids.add(b.readUtf(64));return ids;}
 public static final StreamCodec<RegistryFriendlyByteBuf,Discovery> DISCOVERY=new StreamCodec<>(){
  public void encode(RegistryFriendlyByteBuf b,Discovery p){b.writeVarInt(p.kind().ordinal());b.writeVarLong(p.revision());ids(b,p.macros());ids(b,p.locals());}
  public Discovery decode(RegistryFriendlyByteBuf b){int kind=b.readVarInt();if(kind<0||kind>=Kind.values().length)throw new IllegalArgumentException("invalid discovery packet kind");long revision=b.readVarLong();return new Discovery(Kind.values()[kind],revision,ids(b),ids(b));}
 };
 public static List<Discovery> snapshot(DiscoveryLedger.Snapshot data){var macros=new ArrayList<>(new TreeSet<>(data.macros()));var locals=new ArrayList<>(new TreeSet<>(data.locals()));int parts=Math.max(1,(Math.max(macros.size(),locals.size())+127)/128);var result=new ArrayList<Discovery>();for(int i=0;i<parts;i++){int m=Math.min(macros.size(),i*128),l=Math.min(locals.size(),i*128);Kind kind=parts==1?Kind.SINGLE:i==0?Kind.START:i==parts-1?Kind.END:Kind.PART;result.add(new Discovery(kind,data.revision(),macros.subList(m,Math.min(m+128,macros.size())),locals.subList(l,Math.min(l+128,locals.size()))));}return List.copyOf(result);}
 private GeographyPackets(){}
}
