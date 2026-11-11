package org.humanjourney.core.geography;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.*;
/** Server-to-client only; callbacks are installed by the physical-client entry point. */
public final class GeographyNetwork {
 public interface Receiver{void context(GeographyPackets.Context packet);void discovery(GeographyPackets.Discovery packet);}
 private static Receiver receiver=new Receiver(){public void context(GeographyPackets.Context p){}public void discovery(GeographyPackets.Discovery p){}};
 public static void receiver(Receiver value){receiver=value;}
 private static SimpleChannel channel;
 public static void register(){channel=ChannelBuilder.named("human_journey_core:geography").networkProtocolVersion(1).simpleChannel().play().clientbound().addMain(GeographyPackets.Context.class,GeographyPackets.CONTEXT,(p,c)->receiver.context(p)).addMain(GeographyPackets.Discovery.class,GeographyPackets.DISCOVERY,(p,c)->receiver.discovery(p)).build();}
 public static boolean send(Object packet,ServerPlayer player){try{channel.send(packet,PacketDistributor.PLAYER.with(player));return true;}catch(RuntimeException e){return false;}}
 private GeographyNetwork(){}
}
