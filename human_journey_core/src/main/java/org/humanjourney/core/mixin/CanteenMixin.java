package org.humanjourney.core.mixin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import org.humanjourney.core.water.WaterContainers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toughasnails.item.FilledCanteenItem;
/** Prevent TAN's enchanted-stack auto-replacement from bypassing treatment and dropping custom components. */
@Mixin(value=FilledCanteenItem.class,remap=false)
public class CanteenMixin {
    @Inject(method="inventoryTick",at=@At("HEAD"),cancellable=true)
    private void hjPreserve(ItemStack stack,ServerLevel level,Entity entity,EquipmentSlot slot,CallbackInfo ci){if(WaterContainers.kind(stack)!=null)ci.cancel();}
}
