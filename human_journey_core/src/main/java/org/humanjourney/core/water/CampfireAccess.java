package org.humanjourney.core.water;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
public interface CampfireAccess {
    NonNullList<ItemStack> hjItems();
    int[] hjProgress();
    int[] hjTimes();
}
