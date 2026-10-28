package org.humanjourney.core.mixin;

import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.humanjourney.core.water.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value=CampfireBlockEntity.class,remap=false)
public abstract class CampfireMixin implements CampfireAccess {
    @Shadow @Final private NonNullList<ItemStack> items;
    @Shadow @Final private int[] cookingProgress;
    @Shadow @Final private int[] cookingTime;
    public NonNullList<ItemStack> hjItems(){return items;}
    public int[] hjProgress(){return cookingProgress;}
    public int[] hjTimes(){return cookingTime;}
    @Inject(method="cookTick",at=@At("HEAD"))
    private static void hjCookWater(ServerLevel level,BlockPos pos,BlockState state,CampfireBlockEntity entity,RecipeManager.CachedCheck check,CallbackInfo ci){
        var self=(CampfireAccess)entity;var policy=WaterBalance.current();
        if(!policy.campfire())return;
        for(int i=0;i<self.hjItems().size();i++){
            ItemStack stack=self.hjItems().get(i);ItemStack result=WaterContainers.boiled(stack);if(result.isEmpty())continue;
            self.hjTimes()[i]=policy.campfireTicks();self.hjProgress()[i]++;
            if(self.hjProgress()[i]>=policy.campfireTicks()){
                self.hjItems().set(i,ItemStack.EMPTY);self.hjProgress()[i]=0;
                Containers.dropItemStack(level,pos.getX()+.5,pos.getY()+1.05,pos.getZ()+.5,result);
                level.sendBlockUpdated(pos,state,state,3);
            }
            entity.setChanged();
        }
    }
    @Redirect(method="cookTick",at=@At(value="INVOKE",target="Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private static boolean hjSkipNativeWaterCooking(ItemStack stack){return stack.isEmpty() || WaterContainers.heatGuard(stack);}
}
