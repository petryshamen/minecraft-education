package org.humanjourney.core.juice;
import net.minecraft.core.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
public abstract class JuiceQuantityRecipe extends CustomRecipe {
 private final boolean bottle;
 protected JuiceQuantityRecipe(CraftingBookCategory category,boolean bottle){super(category);this.bottle=bottle;}
 public JuiceCrafting.Plan plan(CraftingInput input){return JuiceCrafting.plan(input,bottle);}
 @Override public boolean matches(CraftingInput input,Level level){return plan(input)!=null;}
 @Override public ItemStack assemble(CraftingInput input,HolderLookup.Provider lookup){var p=plan(input);return p==null?ItemStack.EMPTY:p.output().copy();}
 @Override public NonNullList<ItemStack> getRemainingItems(CraftingInput input){return JuiceCrafting.remainders(input,plan(input)).grid();}
}
