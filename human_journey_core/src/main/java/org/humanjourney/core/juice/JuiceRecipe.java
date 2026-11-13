package org.humanjourney.core.juice;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
/** Original canteen serializer/API remains valid; quantities now use the shared unit planner. */
public final class JuiceRecipe extends JuiceQuantityRecipe {
 public JuiceRecipe(CraftingBookCategory category){super(category,false);}
 public static ItemStack result(CraftingInput input){var p=JuiceCrafting.plan(input,false);return p==null?ItemStack.EMPTY:p.output().copy();}
 @Override public RecipeSerializer<JuiceRecipe> getSerializer(){return JuiceBootstrap.RECIPE.get();}
}
