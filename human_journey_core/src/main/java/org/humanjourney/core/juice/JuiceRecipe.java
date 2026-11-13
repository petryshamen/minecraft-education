package org.humanjourney.core.juice;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** One native dynamic recipe, not a fixed output recipe per juice/tier. */
public final class JuiceRecipe extends CustomRecipe {
 public JuiceRecipe(CraftingBookCategory category){super(category);}
 public static ItemStack result(CraftingInput input){if(input.ingredientCount()!=2)return ItemStack.EMPTY;ItemStack canteen=ItemStack.EMPTY,ingredient=ItemStack.EMPTY;for(var stack:input.items()){if(stack.isEmpty())continue;if(JuiceContainers.fullPurified(stack)){if(!canteen.isEmpty())return ItemStack.EMPTY;canteen=stack;}else{if(!ingredient.isEmpty())return ItemStack.EMPTY;ingredient=stack;}}if(canteen.isEmpty()||ingredient.isEmpty()||JuiceState.marked(ingredient))return ItemStack.EMPTY;var definition=JuiceStore.state().catalog().ingredients().get(BuiltInRegistries.ITEM.getKey(ingredient.getItem()).toString());return definition==null?ItemStack.EMPTY:JuiceContainers.create(canteen,definition);}
 @Override public boolean matches(CraftingInput input,Level level){return !result(input).isEmpty();}
 @Override public ItemStack assemble(CraftingInput input,HolderLookup.Provider lookup){return result(input);}
 @Override public NonNullList<ItemStack> getRemainingItems(CraftingInput input){var remaining=CraftingRecipe.defaultCraftingReminder(input);for(int i=0;i<input.size();i++)if(JuiceContainers.fullPurified(input.getItem(i)))remaining.set(i,ItemStack.EMPTY);return remaining;}
 @Override public RecipeSerializer<JuiceRecipe> getSerializer(){return JuiceBootstrap.RECIPE.get();}
}
