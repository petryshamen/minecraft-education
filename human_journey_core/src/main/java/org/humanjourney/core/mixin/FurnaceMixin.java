package org.humanjourney.core.mixin;

import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.humanjourney.core.water.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

/** Reuse vanilla fuel, container slots, persistence, progress and lit-state handling. */
@Mixin(value=AbstractFurnaceBlockEntity.class,remap=false)
public abstract class FurnaceMixin {
    @Shadow int cookingTimer;
    @Shadow int cookingTotalTime;
    @Unique private boolean hjWaterBurn;
    @Inject(method="canBurn",at=@At("HEAD"),cancellable=true)
    private void hjCanBurn(RegistryAccess registry,RecipeHolder<?> recipe,SingleRecipeInput input,NonNullList<ItemStack> items,int max,CallbackInfoReturnable<Boolean> ci){
        if((Object)this instanceof FurnaceBlockEntity && WaterContainers.heatGuard(items.get(0)))ci.setReturnValue(WaterTreatment.canOutput(items.get(0),items.get(2),max));
    }
    @Inject(method="burn",at=@At("HEAD"),cancellable=true)
    private void hjBurn(RegistryAccess registry,RecipeHolder<?> recipe,SingleRecipeInput input,NonNullList<ItemStack> items,int max,CallbackInfoReturnable<Boolean> ci){
        if((Object)this instanceof FurnaceBlockEntity && WaterContainers.heatGuard(items.get(0))){hjWaterBurn=WaterTreatment.burn(items,max);ci.setReturnValue(hjWaterBurn);}
    }
    @Inject(method="getTotalCookTime",at=@At("HEAD"),cancellable=true)
    private static void hjCookTime(ServerLevel level,AbstractFurnaceBlockEntity furnace,CallbackInfoReturnable<Integer> ci){
        if(furnace instanceof FurnaceBlockEntity && WaterContainers.heatGuard(furnace.getItem(0)))ci.setReturnValue(WaterBalance.current().furnaceTicks());
    }
    @Inject(method="serverTick",at=@At("HEAD"))
    private static void hjRefreshTime(ServerLevel level,BlockPos pos,BlockState state,AbstractFurnaceBlockEntity furnace,CallbackInfo ci){
        if(furnace instanceof FurnaceBlockEntity && WaterContainers.heatGuard(furnace.getItem(0))){
            FurnaceMixin self=(FurnaceMixin)(Object)furnace;self.cookingTotalTime=WaterBalance.current().furnaceTicks();self.cookingTimer=Math.min(self.cookingTimer,self.cookingTotalTime-1);
        }
    }
    @Inject(method="setRecipeUsed",at=@At("HEAD"),cancellable=true)
    private void hjNoPhantomRecipe(RecipeHolder<?> recipe,CallbackInfo ci){if(hjWaterBurn || recipe==null){hjWaterBurn=false;ci.cancel();}}
}
