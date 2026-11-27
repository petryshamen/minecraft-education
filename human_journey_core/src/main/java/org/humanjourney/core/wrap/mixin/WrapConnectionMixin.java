package org.humanjourney.core.wrap.mixin;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.humanjourney.core.wrap.WrapConnection;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Vanilla handleMoveVehicle does not check the player teleport acknowledgement. */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class WrapConnectionMixin implements WrapConnection {
 @Shadow private Vec3 awaitingPositionFromClient;
 @Shadow private Entity lastVehicle;
 @Shadow private double vehicleFirstGoodX,vehicleFirstGoodY,vehicleFirstGoodZ;
 @Shadow private double vehicleLastGoodX,vehicleLastGoodY,vehicleLastGoodZ;
 @Unique private boolean hjWrap$waiting;
 public boolean hjWrap$pending(){return awaitingPositionFromClient!=null;}
 public void hjWrap$resetVehicle(Entity root){
  lastVehicle=root;vehicleFirstGoodX=vehicleLastGoodX=root.getX();vehicleFirstGoodY=vehicleLastGoodY=root.getY();vehicleFirstGoodZ=vehicleLastGoodZ=root.getZ();hjWrap$waiting=true;
 }
 @Inject(method="handleMoveVehicle",at=@At(value="INVOKE",target="Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/server/level/ServerLevel;)V",shift=At.Shift.AFTER),cancellable=true)
 private void hjWrap$ignoreOldVehiclePacket(ServerboundMoveVehiclePacket packet,CallbackInfo ci){
  if(hjWrap$waiting){if(awaitingPositionFromClient!=null)ci.cancel();else hjWrap$waiting=false;}
 }
 @Inject(method="handleAcceptTeleportPacket",at=@At("RETURN"))
 private void hjWrap$finishAcknowledgement(ServerboundAcceptTeleportationPacket packet,CallbackInfo ci){
  if(awaitingPositionFromClient==null)hjWrap$waiting=false;
 }
}
