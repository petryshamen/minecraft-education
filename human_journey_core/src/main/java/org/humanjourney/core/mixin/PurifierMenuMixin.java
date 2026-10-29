package org.humanjourney.core.mixin;
import net.minecraft.world.item.ItemStack;
import org.humanjourney.core.water.WaterContainers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import toughasnails.container.WaterPurifierMenu;
@Mixin(value=WaterPurifierMenu.class,remap=false)
public class PurifierMenuMixin {
    @Inject(method="canPurify",at=@At("HEAD"),cancellable=true)
    private void hjEmptyInputs(ItemStack stack,CallbackInfoReturnable<Boolean> cir){cir.setReturnValue(WaterContainers.empty(stack));}
}
