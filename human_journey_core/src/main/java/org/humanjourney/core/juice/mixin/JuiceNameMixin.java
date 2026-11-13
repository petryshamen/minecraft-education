package org.humanjourney.core.juice.mixin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import org.humanjourney.core.juice.JuiceState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=ItemStack.class,remap=false)
public class JuiceNameMixin {
 @Inject(method="getHoverName",at=@At("HEAD"),cancellable=true)
 private void hjJuiceName(CallbackInfoReturnable<Component> ci){var stack=(ItemStack)(Object)this;if(JuiceState.marked(stack)&&!stack.has(DataComponents.CUSTOM_NAME))ci.setReturnValue(Component.literal(JuiceState.display(stack)));}
}
