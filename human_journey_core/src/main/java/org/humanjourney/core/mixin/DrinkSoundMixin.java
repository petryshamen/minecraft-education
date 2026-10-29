package org.humanjourney.core.mixin;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import toughasnails.item.DrinkItem;
/** TAN DrinkItem lacks onUseTick; add its canteen cadence only for this bottle/juice hierarchy. */
@Mixin(value=DrinkItem.class,remap=false)
public abstract class DrinkSoundMixin extends Item {
    protected DrinkSoundMixin(Properties properties){super(properties);}
    @Override public void onUseTick(Level level,LivingEntity entity,ItemStack stack,int remaining){
        org.humanjourney.core.compat.TanDrinkSounds.tick(level,entity,stack,remaining);
    }
}
