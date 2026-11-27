package org.humanjourney.core.advancement;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.*;
public final class NativeNetwork {
 private static Consumer<NativePackets.Frame> receiver=p->{};private static SimpleChannel channel;
 public static void receiver(Consumer<NativePackets.Frame> value){receiver=value;}
 public static void register(){channel=ChannelBuilder.named("human_journey_core:native_advancements").networkProtocolVersion(1).simpleChannel().play().clientbound().addMain(NativePackets.Frame.class,NativePackets.CODEC,(p,c)->receiver.accept(p)).build();}
 public static boolean send(NativePackets.Frame frame,ServerPlayer player){try{channel.send(frame,PacketDistributor.PLAYER.with(player));return true;}catch(RuntimeException error){return false;}}
 private NativeNetwork(){}
}
