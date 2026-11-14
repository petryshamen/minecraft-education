package org.humanjourney.core.geography.map;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.*;
/** Common transport. The sole serverbound message is a bounded, role-checked resync request. */
public final class MapNetwork {
 public interface Receiver{void frame(MapPackets.Frame frame,Connection connection);}
 private static Receiver receiver=(p,c)->{};private static SimpleChannel channel;
 public static void receiver(Receiver value){receiver=value;}
 public static void register(){channel=ChannelBuilder.named("human_journey_core:geography_map").networkProtocolVersion(1).simpleChannel().play().clientbound().addMain(MapPackets.Frame.class,MapPackets.CODEC,(p,c)->receiver.frame(p,c.getConnection())).serverbound().addMain(MapPackets.Request.class,MapPackets.REQUEST,(p,c)->{var player=c.getSender();if(player!=null)MapServer.request(player);}).build();}
 public static boolean send(MapPackets.Frame frame,ServerPlayer player){try{channel.send(frame,PacketDistributor.PLAYER.with(player));return true;}catch(RuntimeException e){return false;}}
 public static void request(){try{channel.send(MapPackets.Request.INSTANCE,PacketDistributor.SERVER.noArg());}catch(RuntimeException ignored){}}
 private MapNetwork(){}
}
