package org.humanjourney.core.mixin;
import glitchcore.network.CustomPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.humanjourney.core.water.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toughasnails.network.DrinkInWorldPacket;
@Mixin(value=DrinkInWorldPacket.class,remap=false)
public class DrinkPacketMixin implements DrinkPacketAccess {
    @Shadow private BlockPos pos;
    public BlockPos hjPosition(){return pos;}
    @Inject(method="handle(Ltoughasnails/network/DrinkInWorldPacket;Lglitchcore/network/CustomPacket$Context;)V",at=@At("HEAD"),cancellable=true)
    private void hjHandDrink(DrinkInWorldPacket packet,CustomPacket.Context context,CallbackInfo ci){
        context.getPlayer().ifPresent(player->{if(player instanceof ServerPlayer server)WaterDrinking.hand(server,((DrinkPacketAccess)packet).hjPosition());});
        ci.cancel();
    }
}
