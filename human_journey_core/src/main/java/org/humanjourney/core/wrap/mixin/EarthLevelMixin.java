package org.humanjourney.core.wrap.mixin;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;
import org.humanjourney.core.wrap.safety.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/** Outside is unavailable space, never a new registered/saved chunk. Existing disk records untouched. */
@Mixin(Level.class)
public abstract class EarthLevelMixin implements BoundaryHooks.LevelReads {
 private boolean hjEarth$outside(BlockPos pos){return (Object)this instanceof ServerLevel level&&EarthProtection.active(level)&&!SafetyPolicy.readable(pos.getX(),pos.getZ());}
 @Inject(method="getBlockState",at=@At("HEAD"),cancellable=true)
 private void hjEarth$readBlock(BlockPos pos,CallbackInfoReturnable<BlockState> ci){if(hjEarth$outside(pos))ci.setReturnValue(Blocks.VOID_AIR.defaultBlockState());}
 @Inject(method="getFluidState",at=@At("HEAD"),cancellable=true)
 private void hjEarth$readFluid(BlockPos pos,CallbackInfoReturnable<FluidState> ci){if(hjEarth$outside(pos))ci.setReturnValue(Fluids.EMPTY.defaultFluidState());}
 @Inject(method="setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",at=@At("HEAD"),cancellable=true)
 private void hjEarth$writeBlock(BlockPos pos,BlockState state,int flags,int depth,CallbackInfoReturnable<Boolean> ci){if(hjEarth$outside(pos))ci.setReturnValue(false);}
}
