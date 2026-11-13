package org.humanjourney.core.juice;
import net.minecraft.world.item.crafting.*;
public final class JuiceBottleRecipe extends JuiceQuantityRecipe {
 public JuiceBottleRecipe(CraftingBookCategory category){super(category,true);}
 @Override public RecipeSerializer<JuiceBottleRecipe> getSerializer(){return JuiceBootstrap.BOTTLE_RECIPE.get();}
}
