package org.humanjourney.core.water;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
/** Persistent namespaced marker, independent of the current biome and policy. */
public final class SaltWater {
    public static final String KEY="human_journey:water_type";
    public static boolean marked(ItemStack stack){var d=stack.get(DataComponents.CUSTOM_DATA);return d!=null&&d.copyTag().getString(KEY).orElse("").equals("salt");}
    public static void set(ItemStack stack,boolean salt){
        var old=stack.get(DataComponents.CUSTOM_DATA);CompoundTag data=old==null?new CompoundTag():old.copyTag();
        if(salt)data.putString(KEY,"salt");else data.remove(KEY);
        if(data.isEmpty())stack.remove(DataComponents.CUSTOM_DATA);else stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));
    }
    private SaltWater(){}
}
