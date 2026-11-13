package org.humanjourney.core.juice.mixin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.humanjourney.core.juice.JuiceCraftGuard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=CraftingMenu.class,remap=false)
public class JuiceCraftGuardMixin {
 @Inject(method="slotChangedCraftingGrid",at=@At("RETURN"))
 private static void hjJuiceGuard(AbstractContainerMenu menu,ServerLevel level,Player player,CraftingContainer input,ResultContainer result,RecipeHolder<?> recipe,CallbackInfo ci){JuiceCraftGuard.guard(menu,player,input,result);}
}
