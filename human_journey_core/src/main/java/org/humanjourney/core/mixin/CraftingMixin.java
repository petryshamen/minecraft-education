package org.humanjourney.core.mixin;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.humanjourney.core.water.WaterTreatment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(value=CraftingMenu.class,remap=false)
public class CraftingMixin {
    @Inject(method="slotChangedCraftingGrid",at=@At("RETURN"))
    private static void hjGuard(AbstractContainerMenu menu,ServerLevel level,Player player,CraftingContainer input,ResultContainer result,RecipeHolder<?> recipe,CallbackInfo ci){WaterTreatment.guardCraft(menu,player,input,result);}
}
