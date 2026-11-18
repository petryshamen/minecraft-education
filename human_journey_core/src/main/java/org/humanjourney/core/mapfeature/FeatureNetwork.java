package org.humanjourney.core.mapfeature;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.*;
/** Common transport. The sole serverbound message is a bounded, role-checked resync request. */
public final class FeatureNetwork {
 public interface Receiver{void frame(FeaturePackets.Frame frame,Connection connection);}
 private static Receiver receiver=(p,c)->{};private static SimpleChannel channel;
 public static void receiver(Receiver value){receiver=value;}
 public static void register(){channel=ChannelBuilder.named("human_journey_core:map_features").networkProtocolVersion(2).simpleChannel().play().clientbound().addMain(FeaturePackets.Frame.class,FeaturePackets.CODEC,(p,c)->receiver.frame(p,c.getConnection())).serverbound().addMain(FeaturePackets.Request.class,FeaturePackets.REQUEST,(p,c)->{var player=c.getSender();if(player!=null)FeatureServer.request(player);}).build();}
 public static boolean send(FeaturePackets.Frame frame,ServerPlayer player){try{channel.send(frame,PacketDistributor.PLAYER.with(player));return true;}catch(RuntimeException e){return false;}}
 public static void request(){try{channel.send(FeaturePackets.Request.INSTANCE,PacketDistributor.SERVER.noArg());}catch(RuntimeException ignored){}}
 private FeatureNetwork(){}
}
