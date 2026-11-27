package org.humanjourney.core.wrap.mixin;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.*;
import org.humanjourney.core.wrap.safety.SafetyRuntime;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class EarthPacketMixin implements org.humanjourney.core.wrap.safety.BoundaryHooks.Packets {
 @Shadow @Final public ServerPlayer player;
 @Inject(method="handleMovePlayer",at=@At(value="INVOKE",target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V",shift=At.Shift.AFTER),cancellable=true)
 private void hjEarth$player(ServerboundMovePlayerPacket packet,CallbackInfo ci){if(packet.hasPosition()&&!player.isPassenger()&&SafetyRuntime.packet(player,packet.getX(player.getX()),packet.getZ(player.getZ())))ci.cancel();}
 @Inject(method="handleMoveVehicle",at=@At(value="INVOKE",target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V",shift=At.Shift.AFTER),cancellable=true)
 private void hjEarth$vehicle(ServerboundMoveVehiclePacket packet,CallbackInfo ci){if(SafetyRuntime.packet(player,packet.position().x,packet.position().z))ci.cancel();}
}
