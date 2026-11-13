package org.humanjourney.core.juice.mixin;
import org.humanjourney.core.juice.*;
import net.minecraft.world.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.core.NonNullList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
/** Preserve the native take/stat/recipe/shift-click loop; replace only its per-cell consumption/remainders. */
@Mixin(value=ResultSlot.class,remap=false)
public abstract class JuiceResultSlotMixin extends Slot {
 @Shadow @Final private CraftingContainer craftSlots;
 @Unique private JuiceQuantityRecipe hjRecipe;
 @Unique private JuiceCrafting.Plan hjPlan;
 @Unique private java.util.List<ItemStack> hjOverflow=java.util.List.of();
 protected JuiceResultSlotMixin(Container container,int slot,int x,int y){super(container,slot,x,y);}
 @Unique private void hjCapture(){if(container instanceof RecipeCraftingHolder holder){var used=holder.getRecipeUsed();if(used!=null)hjRecipe=used.value() instanceof JuiceQuantityRecipe recipe?recipe:null;}}
 @Override public boolean mayPickup(Player player){if(player.level().isClientSide())return super.mayPickup(player);hjCapture();if(hjRecipe==null)return super.mayPickup(player);var plan=hjRecipe.plan(craftSlots);return plan!=null&&ItemStack.isSameItemSameComponents(plan.output(),getItem());}
 // Native quick craft awards the used recipe BEFORE onTake and clears RecipeCraftingHolder.
 @Inject(method="onQuickCraft(Lnet/minecraft/world/item/ItemStack;I)V",at=@At("HEAD"))
 private void hjRememberQuickCraft(ItemStack stack,int count,CallbackInfo ci){hjCapture();}
 @Inject(method="onTake",at=@At("HEAD"))
 private void hjBegin(Player player,ItemStack output,CallbackInfo ci){hjCapture();hjOverflow=java.util.List.of();hjPlan=hjRecipe==null?null:hjRecipe.plan(craftSlots);if(hjRecipe!=null&&hjPlan==null)throw new IllegalStateException("Stale/invalid juice crafting result");}
 @Inject(method="getRemainingItems",at=@At("HEAD"),cancellable=true)
 private void hjRemainders(CraftingInput trimmed,Level level,CallbackInfoReturnable<NonNullList<ItemStack>> ci){if(hjPlan==null)return;var remainders=JuiceCrafting.remainders(JuiceCrafting.gridInput(craftSlots),hjPlan);hjOverflow=remainders.overflow();ci.setReturnValue(JuiceCrafting.project(remainders.grid(),craftSlots.getWidth(),craftSlots.asPositionedCraftInput()));}
 @Redirect(method="onTake",at=@At(value="INVOKE",target="Lnet/minecraft/world/inventory/CraftingContainer;removeItem(II)Lnet/minecraft/world/item/ItemStack;"))
 private ItemStack hjConsume(CraftingContainer input,int index,int vanillaCount){return JuiceCrafting.remove(input,hjPlan,index,vanillaCount);}
 @Inject(method="onTake",at=@At("RETURN"))
 private void hjEnd(Player player,ItemStack output,CallbackInfo ci){for(var remainder:hjOverflow)if(!player.getInventory().add(remainder)&&!remainder.isEmpty())player.drop(remainder,false);hjOverflow=java.util.List.of();hjPlan=null;hjRecipe=null;}
}
